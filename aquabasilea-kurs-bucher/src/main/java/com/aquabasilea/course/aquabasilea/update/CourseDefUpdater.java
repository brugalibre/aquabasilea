package com.aquabasilea.course.aquabasilea.update;

import com.aquabasilea.course.CourseLocation;
import com.aquabasilea.course.aquabasilea.CourseDef;
import com.aquabasilea.course.aquabasilea.repository.CourseDefRepository;
import com.aquabasilea.course.aquabasilea.repository.mapping.CoursesDefEntityMapper;
import com.aquabasilea.web.extractcourses.AquabasileaCourseExtractor;
import com.aquabasilea.web.extractcourses.model.ExtractedAquabasileaCourses;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CourseDefUpdater {

   private static final Logger LOG = LoggerFactory.getLogger(CourseDefUpdater.class);
   private final Supplier<AquabasileaCourseExtractor> aquabasileaCourseExtractorSupplier;
   private final CourseDefRepository courseDefRepository;
   private final CoursesDefEntityMapper coursesDefEntityMapper;
   private final ExecutorService executorService;
   private boolean isCourseDefUpdateRunning;

   public CourseDefUpdater(Supplier<AquabasileaCourseExtractor> aquabasileaCourseExtractorSupplier, CourseDefRepository courseDefRepository, CoursesDefEntityMapper coursesDefEntityMapper) {
      this.aquabasileaCourseExtractorSupplier = aquabasileaCourseExtractorSupplier;
      this.coursesDefEntityMapper = coursesDefEntityMapper;
      this.courseDefRepository = courseDefRepository;
      this.executorService = Executors.newSingleThreadExecutor();
   }

   /**
    * Updates all {@link CourseDef} according to the aquabasliea-courses which are currently defined on their course-page
    *
    * @param courseLocations the locations to consider
    */
   public void updateAquabasileaCourses(List<CourseLocation> courseLocations) {
      if (isCourseDefUpdateRunning()) {
         LOG.warn("CourseDefs are already being updated, do nothing!");
         return;
      }
      this.executorService.submit(() -> updateCourseDefsAsRunnable(courseLocations));
   }

   private void updateCourseDefsAsRunnable(List<CourseLocation> courseLocations) {
      try {
         LOG.info("Updating course-defs..");
         isCourseDefUpdateRunning = true;
         long start = System.currentTimeMillis();
         updateAquabasileaCoursesInternal(courseLocations);
         Duration duration = Duration.ofMillis(System.currentTimeMillis() - start);
         LOG.info("Updating course-defs done, duration: {}", duration);
      } catch (Exception e) {
         LOG.error("Error while executing the CourseDefUpdater!", e);
      } finally {
         this.isCourseDefUpdateRunning = false;
      }
   }

   private void updateAquabasileaCoursesInternal(List<CourseLocation> courseLocations) {
      List<com.aquabasilea.web.model.CourseLocation> webCourseLocations = map2WebCourseLocation(courseLocations);
      ExtractedAquabasileaCourses extractedAquabasileaCourses = aquabasileaCourseExtractorSupplier.get().extractAquabasileaCourses(webCourseLocations);
      courseDefRepository.deleteAll();
      List<CourseDef> courseDefs = mapAquabasileaCourse2CourseDefs(extractedAquabasileaCourses);
      courseDefRepository.saveAll(courseDefs);
   }

   public boolean isCourseDefUpdateRunning() {
      return isCourseDefUpdateRunning;
   }

   private List<CourseDef> mapAquabasileaCourse2CourseDefs(ExtractedAquabasileaCourses extractedAquabasileaCourses) {
      return coursesDefEntityMapper.mapAquabasileaCourse2CourseDefs(extractedAquabasileaCourses.getAquabasileaCourses())
              .stream()
              .distinct()
              .toList();
   }

   @NotNull
   private static List<com.aquabasilea.web.model.CourseLocation> map2WebCourseLocation(List<CourseLocation> courseLocations) {
      return courseLocations.stream()
              .map(CourseLocation::getWebCourseLocation)
              .collect(Collectors.toList());
   }
}
