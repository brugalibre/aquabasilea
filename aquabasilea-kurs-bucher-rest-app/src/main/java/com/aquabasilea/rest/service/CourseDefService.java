package com.aquabasilea.rest.service;

import com.aquabasilea.course.CourseLocation;
import com.aquabasilea.course.aquabasilea.repository.CourseDefRepository;
import com.aquabasilea.course.aquabasilea.update.CourseDefUpdater;
import com.aquabasilea.rest.model.CourseLocationDto;
import com.aquabasilea.rest.model.course.aquabasilea.CourseDefDto;
import com.aquabasilea.search.ObjectTextSearch;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Objects.isNull;

@Service
public class CourseDefService {

   private final ObjectTextSearch objectTextSearch;
   private final CourseDefUpdater courseDefUpdater;
   private final CourseDefRepository courseDefRepository;
   private List<CourseDefDto> cachedCourseDefDtos;

   private final ExecutorService executorService;
   private boolean isCourseDefUpdateRunning;

   @Autowired
   public CourseDefService(CourseDefUpdater courseDefUpdater, CourseDefRepository courseDefRepository, ObjectTextSearch objectTextSearch) {
      this.courseDefUpdater = courseDefUpdater;
      this.courseDefRepository = courseDefRepository;
      this.objectTextSearch = objectTextSearch;
      this.cachedCourseDefDtos = new ArrayList<>();
      this.executorService = Executors.newFixedThreadPool(1);
      this.isCourseDefUpdateRunning = false;
   }

   public boolean isCourseDefUpdateRunning() {
      return isCourseDefUpdateRunning;
   }

   public void updateCourseDefs(List<CourseLocation> courseLocations) {
      if (isCourseDefUpdateRunning) {
         LOG.warn("CourseDefs are already being updated, do nothing!");
         return;
      }
      executorService.submit(() -> updateCourseDefsAsRunnable(courseLocations));
   }

   private void updateCourseDefsAsRunnable(List<CourseLocation> courseLocations) {
      try {
         isCourseDefUpdateRunning = true;
         updateCourseDefsAsRunnable0(courseLocations);
      } catch (Exception e) {
         LOG.error("Error while executing the CourseDefUpdater!", e);
      } finally {
         this.isCourseDefUpdateRunning = false;
      }
   }

   private void updateCourseDefsAsRunnable0(List<CourseLocation> courseLocations) {
      LOG.info("Update course-defs");
      long start = System.currentTimeMillis();
      courseDefUpdater.updateAquabasileaCourses(courseLocations);
      Duration duration = Duration.ofMillis(System.currentTimeMillis() - start);
      this.cachedCourseDefDtos.clear();
      LOG.info("Update course-defs done, duration: {}", (duration.toMinutes() + "min and " + (60 * duration.toMinutes() - duration.toSeconds()) + "s"));
   }

   public List<CourseDefDto> getCourseDefDtos4Filter(String filter) {
      List<CourseDefDto> allCourseDefDtos = getAllCourseDefDtos();

      if (isNull(filter)) {
         return allCourseDefDtos
                 .stream()
                 .sorted(Comparator.comparing(CourseDefDto::courseName))
                 .toList();
      }
      return objectTextSearch.getWeightedObjects4Filter(allCourseDefDtos, filter);
   }

   public List<CourseLocationDto> getCourseLocationsDtos() {
      return Arrays.stream(CourseLocation.values())
              .map(CourseLocationDto::of)
              .toList();
   }

   private synchronized List<CourseDefDto> getAllCourseDefDtos() {
      if (cachedCourseDefDtos.isEmpty()) {
         this.cachedCourseDefDtos = getAllCourseDefDtosFromRepository();
      }
      return cachedCourseDefDtos;
   }

   @NotNull
   private List<CourseDefDto> getAllCourseDefDtosFromRepository() {
      return courseDefRepository.findAllCourseDefs()
              .stream()
              .map(CourseDefDto::of)
              .toList();
   }
}
