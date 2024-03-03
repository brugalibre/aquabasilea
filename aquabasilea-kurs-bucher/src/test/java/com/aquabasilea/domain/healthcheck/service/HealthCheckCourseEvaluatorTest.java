package com.aquabasilea.domain.healthcheck.service;

import com.aquabasilea.domain.course.model.Course;
import com.aquabasilea.domain.course.model.WeeklyCourses;
import com.aquabasilea.domain.course.repository.WeeklyCoursesRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HealthCheckCourseEvaluatorTest {

   @Test
   void getCourseId4HealthCheck() {
      // Given
      String userId = "dsf";
      Course course = getCourse(true);
      WeeklyCoursesRepository weeklyCoursesRepository = mockWeeklyCoursesRepository(List.of(course), userId);
      HealthCheckCourseEvaluator healthCheckCourseEvaluator = new HealthCheckCourseEvaluator(() -> userId, weeklyCoursesRepository);

      // When
      Optional<String> actualCourseIdOpt = healthCheckCourseEvaluator.getCourseId4HealthCheck();

      // Then
      assertThat(actualCourseIdOpt.isPresent()).isTrue();
   }

   @Test
   void getCourseId4HealthCheckNoCourses() {
      // Given
      String userId = "dsf";
      WeeklyCoursesRepository weeklyCoursesRepository = mockWeeklyCoursesRepository(List.of(), userId);
      HealthCheckCourseEvaluator healthCheckCourseEvaluator = new HealthCheckCourseEvaluator(() -> userId, weeklyCoursesRepository);

      // When
      Optional<String> actualCourseIdOpt = healthCheckCourseEvaluator.getCourseId4HealthCheck();

      // Then
      assertThat(actualCourseIdOpt.isPresent()).isFalse();
   }

   @Test
   void getCourseId4HealthCheckNoCoursesWithCourseDef() {
      // Given
      String userId = "dsf";
      WeeklyCoursesRepository weeklyCoursesRepository = mockWeeklyCoursesRepository(List.of(getCourse(false)), userId);
      HealthCheckCourseEvaluator healthCheckCourseEvaluator = new HealthCheckCourseEvaluator(() -> userId, weeklyCoursesRepository);

      // When
      Optional<String> actualCourseIdOpt = healthCheckCourseEvaluator.getCourseId4HealthCheck();

      // Then
      assertThat(actualCourseIdOpt.isPresent()).isFalse();
   }

   private static Course getCourse(boolean hasCourseDef) {
      Course course = new Course();
      course.setId("123");
      course.setHasCourseDef(hasCourseDef);
      return course;
   }

   private static WeeklyCoursesRepository mockWeeklyCoursesRepository(List<Course> courses, String userId) {
      WeeklyCoursesRepository weeklyCoursesRepository = mock(WeeklyCoursesRepository.class);
      WeeklyCourses weeklyCourses = new WeeklyCourses(userId, courses);

      when(weeklyCoursesRepository.getByUserId(eq(userId))).thenReturn(weeklyCourses);
      return weeklyCoursesRepository;
   }
}