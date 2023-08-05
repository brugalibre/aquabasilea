package com.aquabasilea.domain.coursedef.update.facade;

import com.aquabasilea.domain.course.CourseLocation;
import com.aquabasilea.domain.coursebooker.config.AquabasileaCourseBookerConfig;
import com.aquabasilea.domain.coursedef.model.CourseDef;
import com.aquabasilea.domain.coursedef.model.repository.mapping.CoursesDefEntityMapper;
import com.aquabasilea.domain.coursedef.model.repository.mapping.CoursesDefEntityMapperImpl;
import com.aquabasilea.migrosapi.model.getcourse.request.api.MigrosApiGetCoursesRequest;
import com.aquabasilea.migrosapi.model.getcourse.response.api.MigrosApiGetCoursesResponse;
import com.aquabasilea.migrosapi.service.MigrosApi;
import com.aquabasilea.migrosapi.service.MigrosApiImpl;
import com.aquabasilea.migrosapi.service.security.api.BearerTokenProvider;
import com.aquabasilea.web.extractcourses.AquabasileaCourseExtractor;
import com.aquabasilea.web.extractcourses.impl.AquabasileaCourseExtractorImpl;
import com.aquabasilea.web.extractcourses.model.ExtractedAquabasileaCourses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Supplier;

/**
 * The {@link CourseExtractorFacade} hides the actual implementation which is either a {@link AquabasileaCourseExtractor}
 * or the MiApi
 */
public class CourseExtractorFacade {

   private static final Logger LOG = LoggerFactory.getLogger(CourseExtractorFacade.class);
   private final CoursesDefEntityMapper coursesDefEntityMapper;
   private final Supplier<AquabasileaCourseExtractor> aquabasileaCourseExtractorSupplier;
   private final Supplier<MigrosApi> migrosApiSupplier;
   private final AquabasileaCourseBookerConfig aquabasileaCourseBookerConfig;

   public CourseExtractorFacade(Supplier<AquabasileaCourseExtractor> aquabasileaCourseExtractor, Supplier<MigrosApi> migrosApiSupplier, AquabasileaCourseBookerConfig aquabasileaCourseBookerConfig) {
      this.coursesDefEntityMapper = new CoursesDefEntityMapperImpl();
      this.aquabasileaCourseExtractorSupplier = aquabasileaCourseExtractor;
      this.migrosApiSupplier = migrosApiSupplier;
      this.aquabasileaCourseBookerConfig = aquabasileaCourseBookerConfig.refresh();
   }

   public CourseExtractorFacade(Supplier<AquabasileaCourseExtractor> aquabasileaCourseExtractor, Supplier<MigrosApi> migrosApiSupplier) {
      this(aquabasileaCourseExtractor, migrosApiSupplier, new AquabasileaCourseBookerConfig());
   }

   public static CourseExtractorFacade getCourseExtractorFacade() {
      MigrosApi migrosApi = new MigrosApiImpl(getBearerTokenProvider());
      return new CourseExtractorFacade(AquabasileaCourseExtractorImpl::createAndInitAquabasileaWebNavigator, () -> migrosApi);
   }

   /**
    * Calls the actual {@link CourseDef}-extractor (depending on the configured value), maps the extracted
    * values into {@link CourseDef}s and returns them
    *
    * @param userId          the id of the user for whom {@link CourseDef}s are extracted
    * @param courseLocations the course-location to extract {@link CourseDef}s from
    * @return a {@link List} of {@link CourseDef}s
    */
   public List<CourseDef> extractAquabasileaCourses(String userId, List<CourseLocation> courseLocations) {
      aquabasileaCourseBookerConfig.refresh();
      switch (aquabasileaCourseBookerConfig.getCourseDefExtractorType()) {
         case MIGROS_API -> {
            MigrosApiGetCoursesRequest migrosApiGetCoursesRequest = getMigrosApiGetCoursesRequest(courseLocations);
            MigrosApiGetCoursesResponse migrosApiGetCoursesResponse = migrosApiSupplier.get().getCourses(migrosApiGetCoursesRequest);
            return map2CourseDefsAndSetUserId(userId, migrosApiGetCoursesResponse);
         }
         case AQUABASILEA_WEB -> {
            ExtractedAquabasileaCourses extractedAquabasileaCourses = aquabasileaCourseExtractorSupplier.get().extractAquabasileaCourses(map2CourseLocationNames(courseLocations));
            return map2CourseDefsAndSetUserId(userId, extractedAquabasileaCourses);
         }
      }
      LOG.error("Unsupported course-def-extractor [{}]", aquabasileaCourseBookerConfig.getCourseDefExtractorType());
      return List.of();
   }

   private List<String> map2CourseLocationNames(List<CourseLocation> courseLocations) {
      return courseLocations.stream()
              .map(CourseLocation::name)
              .toList();
   }

   private static MigrosApiGetCoursesRequest getMigrosApiGetCoursesRequest(List<CourseLocation> courseLocations) {
      List<String> courseCenterIds = courseLocations.stream()
              .map(CourseLocation::getId)
              .toList();
      return MigrosApiGetCoursesRequest.of(courseCenterIds);
   }

   private List<CourseDef> map2CourseDefsAndSetUserId(String userId, ExtractedAquabasileaCourses extractedAquabasileaCourses) {
      return setUserId(userId, map2CourseDefs(extractedAquabasileaCourses));
   }

   private List<CourseDef> map2CourseDefsAndSetUserId(String userId, MigrosApiGetCoursesResponse migrosApiGetCoursesResponse) {
      return setUserId(userId, map2CourseDefs(migrosApiGetCoursesResponse));
   }

   private List<CourseDef> setUserId(String userId, List<CourseDef> courseDefs) {
      return courseDefs
              .stream()
              .map(courseDef -> courseDef.setUserId(userId))
              .toList();
   }

   private List<CourseDef> map2CourseDefs(ExtractedAquabasileaCourses extractedAquabasileaCourses) {
      return coursesDefEntityMapper.mapAquabasileaCourses2CourseDefs(extractedAquabasileaCourses.getAquabasileaCourses());
   }

   private List<CourseDef> map2CourseDefs(MigrosApiGetCoursesResponse migrosApiGetCoursesResponse) {
      return coursesDefEntityMapper.mapMigrosCourses2CourseDefs(migrosApiGetCoursesResponse.courses());
   }

   private static BearerTokenProvider getBearerTokenProvider() {
      return (username, userPwd) -> {
         throw new IllegalStateException("Read only MigrosApi!");
      };
   }

}
