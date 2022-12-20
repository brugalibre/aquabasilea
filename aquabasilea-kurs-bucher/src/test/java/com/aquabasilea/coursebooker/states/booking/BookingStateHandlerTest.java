package com.aquabasilea.coursebooker.states.booking;

import com.aquabasilea.coursebooker.states.CourseBookingState;
import com.aquabasilea.model.course.weeklycourses.Course;
import com.aquabasilea.model.course.weeklycourses.Course.CourseBuilder;
import com.aquabasilea.model.course.weeklycourses.WeeklyCourses;
import com.aquabasilea.model.course.weeklycourses.repository.WeeklyCoursesRepository;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseBookingEndResult;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Optional;

import static com.aquabasilea.web.bookcourse.impl.select.result.CourseClickedResult.COURSE_BOOKING_SKIPPED;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookingStateHandlerTest {
   private static final String USER_ID = "123";

   @Test
   void testBookCourseWithoutCourseDef() {

      // Given
      TestCaseBuilder tcb = new TestCaseBuilder()
              .withCourse(CourseBuilder.builder()
                      .withCourseDate(LocalDateTime.of(LocalDate.now(), LocalTime.of(15, 15)))
                      .withIsPaused(true)
                      .withHasCourseDef(false)
                      .withCourseName("Kurs-99")
                      .withId("1")
                      .build())
              .build();

      // When
      CourseBookingEndResult actualCourseBookingEndResult = tcb.bookingStateHandler.bookCourse(USER_ID, tcb.weeklyCourses.getCourses().get(0), tcb.currentBookingState);

      // When
      assertThat(actualCourseBookingEndResult.getCourseClickedResult(), is(COURSE_BOOKING_SKIPPED));
   }

   @Test
   void resumePrevCourses() {
      // Given
      String currentCourse1Id = "1";
      String course2Id = "2";
      String course3Id = "3";
      String course4Id = "4";
      String course6Id = "6";
      Course currentCourse = CourseBuilder.builder()
              .withCourseDate(LocalDateTime.of(2022, Month.APRIL, 15, 15, 15))// FRIDAY
              .withCourseName("Kurs-abcd")
              .withId(currentCourse1Id)
              .build();

      TestCaseBuilder tcb = new TestCaseBuilder()
              .withCourse(CourseBuilder.builder()
                      .withCourseDate(LocalDateTime.of(2022, Month.APRIL, 11, 15, 15)) // MONDAY
                      .withIsPaused(true)
                      .withHasCourseDef(true)
                      .withCourseName("Kurs-11")
                      .withId(course2Id)
                      .build())
              .withCourse(CourseBuilder.builder()
                      .withCourseDate(LocalDateTime.of(2022, Month.APRIL, 17, 15, 15))// SUNDAY
                      .withCourseName("Kurs-1")
                      .withHasCourseDef(true)
                      .withId(course3Id)
                      .build())
              .withCourse(CourseBuilder.builder()
                      .withCourseDate(LocalDateTime.of(2022, Month.APRIL, 16, 15, 15))// SATURDAY
                      .withCourseName("Kurs-1")
                      .withIsPaused(true)
                      .withHasCourseDef(true)
                      .withId(course6Id)
                      .build())
              .withCourse(CourseBuilder.builder()
                      .withCourseDate(LocalDateTime.of(2022, Month.APRIL, 14, 15, 15))// THURSDAY
                      .withIsPaused(true)
                      .withHasCourseDef(true)
                      .withCourseName("Kurs-99")
                      .withId(course4Id)
                      .build())
              .withCourse(currentCourse)
              .build();

      // When
      tcb.bookingStateHandler.resumeCoursesUntil(USER_ID, currentCourse);

      // Then
      WeeklyCourses weeklyCourses = tcb.weeklyCoursesRepository.getByUserId(USER_ID);
      Optional<Course> course4Id1 = getCourse4Id(course4Id, weeklyCourses);
      Optional<Course> course2Id1 = getCourse4Id(course2Id, weeklyCourses);
      Optional<Course> course6Id1 = getCourse4Id(course6Id, weeklyCourses);
      assertThat(course4Id1.isPresent(), is(true));
      assertThat(course4Id1.get().getIsPaused(), is(false));
      assertThat(course2Id1.isPresent(), is(true));
      assertThat(course2Id1.get().getIsPaused(), is(false));
      assertThat(course6Id1.isPresent(), is(true));
      assertThat(course6Id1.get().getIsPaused(), is(true));
   }

   private static Optional<Course> getCourse4Id(String course4Id, WeeklyCourses weeklyCourses) {
      return weeklyCourses.getCourses()
              .stream()
              .filter(course -> course.getId().equals(course4Id))
              .findFirst();
   }

   private static class TestCaseBuilder {
      private final WeeklyCoursesRepository weeklyCoursesRepository;
      private final BookingStateHandler bookingStateHandler;
      private final WeeklyCourses weeklyCourses;
      private final CourseBookingState currentBookingState;

      private TestCaseBuilder() {
         this.weeklyCoursesRepository = mock(WeeklyCoursesRepository.class);
         this.bookingStateHandler = new BookingStateHandler(weeklyCoursesRepository, null);
         this.weeklyCourses = new WeeklyCourses(USER_ID);
         this.currentBookingState = CourseBookingState.BOOKING;
      }

      private TestCaseBuilder withCourse(Course course) {
         this.weeklyCourses.addCourse(course);
         return this;
      }

      private TestCaseBuilder build() {
         mockWeeklyCoursesRepository();
         return this;
      }

      private void mockWeeklyCoursesRepository() {
         when(weeklyCoursesRepository.getByUserId(USER_ID)).thenReturn(weeklyCourses);
      }
   }
}