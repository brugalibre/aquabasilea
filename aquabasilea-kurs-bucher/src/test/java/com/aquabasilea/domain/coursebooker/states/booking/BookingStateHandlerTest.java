package com.aquabasilea.domain.coursebooker.states.booking;

import com.aquabasilea.domain.course.model.Course;
import com.aquabasilea.domain.course.model.Course.CourseBuilder;
import com.aquabasilea.domain.course.model.WeeklyCourses;
import com.aquabasilea.domain.course.repository.WeeklyCoursesRepository;
import com.aquabasilea.domain.coursebooker.model.booking.BookingContext;
import com.aquabasilea.domain.coursebooker.model.booking.CourseBookContainer;
import com.aquabasilea.domain.coursebooker.model.booking.CourseBookDetails;
import com.aquabasilea.domain.coursebooker.model.booking.result.CourseBookingResultDetails;
import com.aquabasilea.domain.coursebooker.model.booking.result.CourseBookingResultDetailsImpl;
import com.aquabasilea.domain.coursebooker.model.state.CourseBookingState;
import com.aquabasilea.domain.coursebooker.states.booking.facade.CourseBookerFacade;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.Optional;

import static com.aquabasilea.domain.coursebooker.model.booking.result.CourseBookResult.BOOKED;
import static com.aquabasilea.domain.coursebooker.model.booking.result.CourseBookResult.BOOKING_SKIPPED;
import static com.aquabasilea.test.TestConstants.FITNESSPARK_GLATTPARK;
import static com.aquabasilea.test.TestConstants.MIGROS_FITNESSCENTER_AQUABASILEA;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookingStateHandlerTest {
   private static final String USER_ID = "123";
   public static final String COURSE_INSTRUCTOR = "Kas";

   @Test
   void testBookCourseWithIdAndCheckResumedCourses() {

      // Given
      LocalDateTime courseDate = LocalDateTime.of(LocalDate.now(), LocalTime.of(15, 15));
      String courseName1 = "Kurs-99";
      String courseName2 = "Kurs-88";
      String courseName3 = "Kurs-77";
      String courseId1 = "1";
      String courseId2 = "2";
      String courseId3 = "3";
      CourseBookerFacade courseBookerFacade = mock(CourseBookerFacade.class);
      CourseBookingResultDetails courseBookingResultDetails = CourseBookingResultDetailsImpl.of(BOOKED, courseName1, null);
      CourseBookContainer expectedCourseBookContainer = new CourseBookContainer(new CourseBookDetails(courseName1, COURSE_INSTRUCTOR,
              courseDate, FITNESSPARK_GLATTPARK), new BookingContext(false));
      when(courseBookerFacade.bookCourse(eq(expectedCourseBookContainer))).thenReturn(courseBookingResultDetails);
      TestCaseBuilder tcb = new TestCaseBuilder()
              .withCourse(CourseBuilder.builder()
                      .withCourseDate(courseDate)
                      .withCourseName(courseName1)
                      .withId(courseId1)
                      .withCourseLocation(FITNESSPARK_GLATTPARK)
                      .withCourseInstructor(COURSE_INSTRUCTOR)
                      .withHasCourseDef(true)
                      .withIsPaused(false)
                      .build())
              .withCourse(CourseBuilder.builder()
                      .withCourseDate(courseDate.minusHours(5))
                      .withCourseName(courseName2)
                      .withId(courseId2)
                      .withCourseInstructor(COURSE_INSTRUCTOR)
                      .withCourseLocation(FITNESSPARK_GLATTPARK)
                      .withHasCourseDef(true)
                      .withIsPaused(true)
                      .build())
              .withCourse(CourseBuilder.builder()
                      .withCourseDate(courseDate.plusHours(5))
                      .withCourseName(courseName3)
                      .withId(courseId3)
                      .withCourseInstructor(COURSE_INSTRUCTOR)
                      .withCourseLocation(FITNESSPARK_GLATTPARK)
                      .withHasCourseDef(true)
                      .withIsPaused(true)
                      .build())
              .withCourseBookingFacade(courseBookerFacade)
              .build();

      // When
      CourseBookingResultDetails actualCourseBookingResultDetails = tcb.bookingStateHandler.bookCourse(USER_ID, tcb.weeklyCourses.getCourses().get(0).getId(), tcb.currentBookingState);

      // When
      assertThat(actualCourseBookingResultDetails.getCourseBookResult(), is(BOOKED));
      WeeklyCourses weeklyCourses = tcb.weeklyCoursesRepository.getByUserId(USER_ID);
      // Earliest course must be resumed
      assertThat(weeklyCourses.getCourseById(courseId2).getIsPaused(), is(false));
      // But the last not
      assertThat(weeklyCourses.getCourseById(courseId3).getIsPaused(), is(true));
   }

   @Test
   void testBookCourseWithoutCourseDef() {

      // Given
      TestCaseBuilder tcb = new TestCaseBuilder()
              .withCourse(CourseBuilder.builder()
                      .withCourseDate(LocalDateTime.of(LocalDate.now(), LocalTime.of(15, 15)))
                      .withIsPaused(true)
                      .withHasCourseDef(false)
                      .withCourseName("Kurs-99")
                      .withCourseInstructor(COURSE_INSTRUCTOR)
                      .withCourseLocation(MIGROS_FITNESSCENTER_AQUABASILEA)
                      .withId("1")
                      .build())
              .build();

      // When
      CourseBookingResultDetails actualCourseBookingResultDetails = tcb.bookingStateHandler.bookCourse(USER_ID, tcb.weeklyCourses.getCourses().get(0), tcb.currentBookingState);

      // When
      assertThat(actualCourseBookingResultDetails.getCourseBookResult(), is(BOOKING_SKIPPED));
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
              .withCourseInstructor(COURSE_INSTRUCTOR)
              .withCourseLocation(MIGROS_FITNESSCENTER_AQUABASILEA)
              .build();

      TestCaseBuilder tcb = new TestCaseBuilder()
              .withCourse(CourseBuilder.builder()
                      .withCourseDate(LocalDateTime.of(2022, Month.APRIL, 11, 15, 15)) // MONDAY
                      .withIsPaused(true)
                      .withHasCourseDef(true)
                      .withCourseName("Kurs-11")
                      .withId(course2Id)
                      .withCourseInstructor(COURSE_INSTRUCTOR)
                      .withCourseLocation(MIGROS_FITNESSCENTER_AQUABASILEA)
                      .build())
              .withCourse(CourseBuilder.builder()
                      .withCourseDate(LocalDateTime.of(2022, Month.APRIL, 17, 15, 15))// SUNDAY
                      .withCourseName("Kurs-1")
                      .withHasCourseDef(true)
                      .withId(course3Id)
                      .withCourseInstructor(COURSE_INSTRUCTOR)
                      .withCourseLocation(MIGROS_FITNESSCENTER_AQUABASILEA)
                      .build())
              .withCourse(CourseBuilder.builder()
                      .withCourseDate(LocalDateTime.of(2022, Month.APRIL, 16, 15, 15))// SATURDAY
                      .withCourseName("Kurs-1")
                      .withIsPaused(true)
                      .withHasCourseDef(true)
                      .withId(course6Id)
                      .withCourseInstructor(COURSE_INSTRUCTOR)
                      .withCourseLocation(MIGROS_FITNESSCENTER_AQUABASILEA)
                      .build())
              .withCourse(CourseBuilder.builder()
                      .withCourseDate(LocalDateTime.of(2022, Month.APRIL, 14, 15, 15))// THURSDAY
                      .withIsPaused(true)
                      .withHasCourseDef(true)
                      .withCourseName("Kurs-99")
                      .withId(course4Id)
                      .withCourseInstructor(COURSE_INSTRUCTOR)
                      .withCourseLocation(MIGROS_FITNESSCENTER_AQUABASILEA)
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
      private final WeeklyCourses weeklyCourses;
      private final CourseBookingState currentBookingState;
      private CourseBookerFacade courseBookerFacade;
      private BookingStateHandler bookingStateHandler;

      private TestCaseBuilder() {
         this.weeklyCoursesRepository = mock(WeeklyCoursesRepository.class);
         this.weeklyCourses = new WeeklyCourses(USER_ID);
         this.currentBookingState = CourseBookingState.BOOKING;
         this.courseBookerFacade = null;
      }

      private TestCaseBuilder withCourse(Course course) {
         this.weeklyCourses.addCourse(course);
         return this;
      }

      private TestCaseBuilder build() {
         mockWeeklyCoursesRepository();
         this.bookingStateHandler = new BookingStateHandler(weeklyCoursesRepository, courseBookerFacade);
         return this;
      }

      private void mockWeeklyCoursesRepository() {
         when(weeklyCoursesRepository.getByUserId(USER_ID)).thenReturn(weeklyCourses);
      }

      public TestCaseBuilder withCourseBookingFacade(CourseBookerFacade courseBookerFacade) {
         this.courseBookerFacade = courseBookerFacade;
         return this;
      }
   }
}