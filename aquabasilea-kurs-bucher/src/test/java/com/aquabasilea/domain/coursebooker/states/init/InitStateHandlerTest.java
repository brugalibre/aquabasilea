package com.aquabasilea.domain.coursebooker.states.init;

import com.aquabasilea.domain.course.Course;
import com.aquabasilea.domain.course.WeeklyCourses;
import com.aquabasilea.domain.course.repository.WeeklyCoursesRepository;
import com.aquabasilea.domain.coursebooker.states.CourseBookingState;
import com.aquabasilea.domain.coursebooker.config.AquabasileaCourseBookerConfig;
import com.aquabasilea.domain.coursedef.model.CourseDef;
import com.aquabasilea.domain.coursedef.model.repository.CourseDefRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.aquabasilea.domain.course.CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

class InitStateHandlerTest {

   private static final String USER_ID = "123";

   @Test
   void testGetLocalDateTimeCourseTimeIsOneHourAfterNow() {
      // Given
      LocalDateTime now = LocalDateTime.now().plusHours(1);
      String courseId = UUID.randomUUID().toString();
      TestCaseBuilder tcb = new TestCaseBuilder()
              .withWeeklyCourses(new WeeklyCourses(USER_ID, List.of(Course.CourseBuilder.builder()
                      .withCourseDate(now)
                      .withCourseName("Kurs-55")
                      .withId(courseId)
                      .build())))
              .build();

      // When
      InitializationResult initializationResult = tcb.initStateHandler.evaluateNextCourseAndState(USER_ID);

      // Then, assert the course date today in 7 days
      assertThat(initializationResult.getNextCourseBookingState(), is(not(CourseBookingState.PAUSED)));
      assertThat(initializationResult.getCurrentCourse(), is(notNullValue()));
      assertThat(initializationResult.getCurrentCourse().getCourseDate().getDayOfMonth(), is(now.plusDays(7).getDayOfMonth()));
      assertThat(initializationResult.getCurrentCourse().getCourseDate().getMonthValue(), is(now.plusDays(7).getMonthValue()));
      verify(tcb.aquabasileaCourseBookerConfig).refresh();
   }

   @Test
   void evaluateNextCourseAndStateCourseDayIsCurrentWeekDay_CurrentCourseShouldBeAWeekInFuture() {
      // Given
      LocalDateTime now = LocalDateTime.now()
              .plusDays(1)
              .plusMinutes(10);
      // The course takes place tomorrow, and we are less than 24h earlier
      String courseId = UUID.randomUUID().toString();
      TestCaseBuilder tcb = new TestCaseBuilder()
              .withWeeklyCourses(new WeeklyCourses(USER_ID, List.of(Course.CourseBuilder.builder()
                      .withCourseDate(now)
                      .withCourseName("Kurs-11")
                      .withId(courseId)
                      .build())))
              .build();

      // When
      InitializationResult initializationResult = tcb.initStateHandler.evaluateNextCourseAndState(USER_ID);

      // Then
      assertThat(initializationResult.getNextCourseBookingState(), is(not(CourseBookingState.PAUSED)));
      assertThat(initializationResult.getCurrentCourse(), is(notNullValue()));
      assertThat(initializationResult.getCurrentCourse().getId(), is(courseId));
   }

   @Test
   void testEvaluateNextCourseAndStateAndUpdateCourseWithoutCourseDef() {
      // Given
      // The course takes place tomorrow, and we are more than 24h earlier
      LocalDateTime courseDate = LocalDateTime.now().plusDays(2);
      String courseId = UUID.randomUUID().toString();
      String courseName = "Kurs-51";
      TestCaseBuilder tcb = new TestCaseBuilder()
              .withWeeklyCourses(new WeeklyCourses(USER_ID, List.of(Course.CourseBuilder.builder()
                      .withCourseDate(courseDate)
                      .withCourseName(courseName)
                      .withCourseLocation(MIGROS_FITNESSCENTER_AQUABASILEA)
                      .withId(courseId)
                      .withHasCourseDef(false)
                      .build())))
              .withCourseDef(new CourseDef("id", USER_ID, courseDate, MIGROS_FITNESSCENTER_AQUABASILEA, courseName, "peter"))
              .build();

      // When
      tcb.initStateHandler.updateCoursesHasCourseDef(tcb.weeklyCourses);

      // Then
      assertThat(tcb.weeklyCourses.getCourses().get(0).getHasCourseDef(), is(true));
   }

   private static class TestCaseBuilder {

      private final WeeklyCoursesRepository weeklyCoursesRepository;
      private final List<CourseDef> courseDefs;
      private final CourseDefRepository courseDefRepository;
      private final AquabasileaCourseBookerConfig aquabasileaCourseBookerConfig;
      private WeeklyCourses weeklyCourses;
      private InitStateHandler initStateHandler;

      private TestCaseBuilder() {
         this.weeklyCoursesRepository = mock(WeeklyCoursesRepository.class);
         this.courseDefRepository = mock(CourseDefRepository.class);
         this.aquabasileaCourseBookerConfig = spy(new AquabasileaCourseBookerConfig());
         this.courseDefs = new ArrayList<>();
      }

      private TestCaseBuilder withWeeklyCourses(WeeklyCourses weeklyCourses) {
         this.weeklyCourses = weeklyCourses;
         return this;
      }

      private TestCaseBuilder build() {
         when(weeklyCoursesRepository.getByUserId(USER_ID)).thenReturn(weeklyCourses);
         when(courseDefRepository.getAllByUserId(USER_ID)).thenReturn(courseDefs);
         this.initStateHandler = new InitStateHandler(weeklyCoursesRepository, courseDefRepository, aquabasileaCourseBookerConfig);
         return this;
      }

      public TestCaseBuilder withCourseDef(CourseDef courseDef) {
         this.courseDefs.add(courseDef);
         return this;
      }
   }
}