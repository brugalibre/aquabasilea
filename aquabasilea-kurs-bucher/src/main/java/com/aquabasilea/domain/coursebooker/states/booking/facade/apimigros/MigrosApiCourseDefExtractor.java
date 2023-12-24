package com.aquabasilea.domain.coursebooker.states.booking.facade.apimigros;

import com.aquabasilea.domain.coursebooker.states.booking.facade.apimigros.mapping.MigrosCourseMapper;
import com.aquabasilea.domain.courselocation.model.CourseLocation;
import com.aquabasilea.domain.coursebooker.states.booking.facade.CourseDefExtractorFacade;
import com.aquabasilea.domain.coursedef.model.CourseDef;
import com.aquabasilea.migrosapi.api.v1.model.getcourse.request.MigrosApiGetCoursesRequest;
import com.aquabasilea.migrosapi.api.v1.model.getcourse.response.MigrosApiGetCoursesResponse;
import com.aquabasilea.migrosapi.api.v1.model.security.AuthenticationContainer;
import com.aquabasilea.migrosapi.api.v1.service.MigrosApi;

import java.util.List;

/**
 * The {@link MigrosApiCourseDefExtractor} hides the migros-api specific implementation of a {@link CourseDefExtractorFacade}
 */
public class MigrosApiCourseDefExtractor implements CourseDefExtractorFacade {
   private final AuthenticationContainerRegistry authenticationContainerRegistry;
   private final MigrosCourseMapper migrosCourseMapper;
   private final MigrosApi migrosApi;

   public MigrosApiCourseDefExtractor(MigrosApi migrosApi, AuthenticationContainerRegistry authenticationContainerRegistry,
                                      MigrosCourseMapper migrosCourseMapper) {
      this.migrosCourseMapper = migrosCourseMapper;
      this.authenticationContainerRegistry = authenticationContainerRegistry;
      this.migrosApi = migrosApi;
   }

   public List<CourseDef> getCourseDefs(String userId, List<CourseLocation> courseLocations) {
      AuthenticationContainer authenticationContainer = authenticationContainerRegistry.getAuthenticationContainerForUserId(userId);
      MigrosApiGetCoursesRequest migrosApiGetCoursesRequest = getMigrosApiGetCoursesRequest(courseLocations);
      MigrosApiGetCoursesResponse migrosApiGetCoursesResponse = migrosApi.getCourses(authenticationContainer, migrosApiGetCoursesRequest);
      return map2CourseDefsAndSetUserId(userId, migrosApiGetCoursesResponse);
   }

   private static MigrosApiGetCoursesRequest getMigrosApiGetCoursesRequest(List<CourseLocation> courseLocations) {
      List<String> courseCenterIds = courseLocations.stream()
              .map(CourseLocation::centerId)
              .toList();
      return MigrosApiGetCoursesRequest.of(courseCenterIds);
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

   private List<CourseDef> map2CourseDefs(MigrosApiGetCoursesResponse migrosApiGetCoursesResponse) {
      return migrosCourseMapper.mapMigrosCourses2CourseDefs(migrosApiGetCoursesResponse.courses());
   }
}
