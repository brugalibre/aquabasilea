package com.aquabasilea.domain.healthcheck.service;

import com.aquabasilea.domain.course.model.Course;
import com.aquabasilea.domain.course.repository.WeeklyCoursesRepository;

import java.util.Optional;
import java.util.function.Supplier;

public class HealthCheckCourseEvaluator {
   private final Supplier<String> healthCheckUserIdEvaluator;
   private final WeeklyCoursesRepository weeklyCoursesRepository;

   public HealthCheckCourseEvaluator(Supplier<String> healthCheckUserIdEvaluator, WeeklyCoursesRepository weeklyCoursesRepository) {
      this.weeklyCoursesRepository = weeklyCoursesRepository;
      this.healthCheckUserIdEvaluator = healthCheckUserIdEvaluator;
   }

   /**
    * @return an optional course-id which is used for the health-check
    */
   public Optional<String> getCourseId4HealthCheck() {
      return weeklyCoursesRepository.getByUserId(healthCheckUserIdEvaluator.get())
              .getCourses()
              .stream()
              .filter(Course::getHasCourseDef)
              .map(Course::getId)
              .findFirst();
   }
}
