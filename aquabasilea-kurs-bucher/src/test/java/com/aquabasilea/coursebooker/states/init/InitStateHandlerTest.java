package com.aquabasilea.coursebooker.states.init;

import com.aquabasilea.course.Course;
import com.aquabasilea.course.WeeklyCourses;
import com.aquabasilea.course.repository.WeeklyCoursesRepository;
import com.aquabasilea.coursebooker.config.AquabasileaCourseBookerConfig;
import com.aquabasilea.coursebooker.states.CourseBookingState;
import com.aquabasilea.util.DateUtil;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InitStateHandlerTest {

   @Test
   void evaluateNextCourseAndStateCourseDayIsCurrentWeekDay_CurrentCourseShouldBeAWeekInFuture() {
      // Given
      LocalDateTime now = LocalDateTime.now();
      // The course takes place tomorrow, and we are less than 24h earlier
      String courseTime = DateUtil.getTimeAsString(now.plusDays(1).plusMinutes(10));
      String courseId = UUID.randomUUID().toString();
      TestCaseBuilder tcb = new TestCaseBuilder()
              .withWeeklyCourses(new WeeklyCourses(List.of(Course.CourseBuilder.builder()
                      .withTimeOfTheDay(courseTime)
                      .withDayOfWeek(now.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMAN))
                      .withCourseName("Kurs-11")
                      .withId(courseId)
                      .build())))
              .build();

      InitStateHandler initStateHandler = new InitStateHandler(tcb.weeklyCoursesRepository, tcb.aquabasileaCourseBookerConfig);

      // When
      InitializationResult initializationResult = initStateHandler.evaluateNextCourseAndState();

      // Then
      assertThat(initializationResult.getNextCourseBookingState(), is(not(CourseBookingState.PAUSED)));
      assertThat(initializationResult.getCurrentCourse(), is(notNullValue()));
      assertThat(initializationResult.getCurrentCourse().getId(), is(courseId));
   }

   private static class TestCaseBuilder {

      private final WeeklyCoursesRepository weeklyCoursesRepository;
      private final AquabasileaCourseBookerConfig aquabasileaCourseBookerConfig;
      private WeeklyCourses weeklyCourses;

      private TestCaseBuilder() {
         this.weeklyCoursesRepository = mock(WeeklyCoursesRepository.class);
         this.aquabasileaCourseBookerConfig = new AquabasileaCourseBookerConfig();
      }

      private TestCaseBuilder withWeeklyCourses(WeeklyCourses weeklyCourses) {
         this.weeklyCourses = weeklyCourses;
         return this;
      }

      private TestCaseBuilder build() {
         when(weeklyCoursesRepository.findFirstWeeklyCourses()).thenReturn(this.weeklyCourses);
         return this;
      }
   }
}