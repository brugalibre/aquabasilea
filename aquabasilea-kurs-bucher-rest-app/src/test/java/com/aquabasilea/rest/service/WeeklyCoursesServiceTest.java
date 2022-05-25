package com.aquabasilea.rest.service;

import com.aquabasilea.course.aquabasilea.CourseDef;
import com.aquabasilea.course.user.Course;
import com.aquabasilea.course.user.WeeklyCourses;
import com.aquabasilea.course.user.repository.WeeklyCoursesRepository;
import com.aquabasilea.coursebooker.AquabasileaCourseBooker;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.aquabasilea.course.CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

class WeeklyCoursesServiceTest {

   public static final String TIME_OF_THE_DAY_15_15 = "15:15";
   public static final String COURSE_NAME_WITH_COURSE_DEF = "Kurs-1";

   @Test
   void updateCoursesAfterCourseDefUpdate() {
      // Given
      LocalDateTime now = LocalDateTime.now();
      LocalDateTime tomorrow = now.plusDays(1);
      TestCaseBuilder tcb = new TestCaseBuilder()
              .withCourse(Course.CourseBuilder.builder()
                      .withTimeOfTheDay(TIME_OF_THE_DAY_15_15)
                      .withDayOfWeek(now.getDayOfWeek())
                      .withIsPaused(true)
                      .withHasCourseDef(true)
                      .withCourseName("Kurs-99")
                      .withId("1")
                      .withCourseLocation(MIGROS_FITNESSCENTER_AQUABASILEA)
                      .build())
              .withCourse(Course.CourseBuilder.builder()
                      .withTimeOfTheDay(TIME_OF_THE_DAY_15_15)
                      .withDayOfWeek(tomorrow.getDayOfWeek())
                      .withIsPaused(true)
                      .withHasCourseDef(true)
                      .withCourseName(COURSE_NAME_WITH_COURSE_DEF)
                      .withId("1")
                      .withCourseLocation(MIGROS_FITNESSCENTER_AQUABASILEA)
                      .build())
              .withUpdatedCourseDef(new CourseDef(now.toLocalDate(), " - ", MIGROS_FITNESSCENTER_AQUABASILEA, ""))
              .withUpdatedCourseDef(new CourseDef(tomorrow.toLocalDate(), TIME_OF_THE_DAY_15_15, MIGROS_FITNESSCENTER_AQUABASILEA, COURSE_NAME_WITH_COURSE_DEF))
              .build();

      // When
      tcb.weeklyCoursesService.updateCoursesAfterCourseDefUpdate(tcb.updatedCourseDefs);

      // Then
      assertThat(tcb.weeklyCourses.getCourses().get(0).getHasCourseDef(), is(false));
      assertThat(tcb.weeklyCourses.getCourses().get(1).getHasCourseDef(), is(true));
      verify(tcb.aquabasileaCourseBooker).refreshCourses();
      verify(tcb.weeklyCoursesRepository).save(any());
   }

   private static class TestCaseBuilder {
      private final WeeklyCoursesRepository weeklyCoursesRepository;
      private final WeeklyCourses weeklyCourses;
      private final WeeklyCoursesService weeklyCoursesService;
      private final List<CourseDef> updatedCourseDefs;
      private final AquabasileaCourseBooker aquabasileaCourseBooker;

      private TestCaseBuilder() {
         this.weeklyCoursesRepository = mock(WeeklyCoursesRepository.class);
         this.weeklyCourses = new WeeklyCourses();
         this.aquabasileaCourseBooker = mock(AquabasileaCourseBooker.class);
         this.weeklyCoursesService = new WeeklyCoursesService(weeklyCoursesRepository, aquabasileaCourseBooker , null);
         this.updatedCourseDefs = new ArrayList<>();
      }

      private TestCaseBuilder withCourse(Course course) {
         this.weeklyCourses.addCourse(course);
         return this;
      }

      private TestCaseBuilder withUpdatedCourseDef(CourseDef courseDef) {
         this.updatedCourseDefs.add(courseDef);
         return this;
      }

      private TestCaseBuilder build() {
         mockWeeklyCoursesRepository();
         return this;
      }

      private void mockWeeklyCoursesRepository() {
         when(weeklyCoursesRepository.findFirstWeeklyCourses()).thenReturn(weeklyCourses);
      }
   }
}