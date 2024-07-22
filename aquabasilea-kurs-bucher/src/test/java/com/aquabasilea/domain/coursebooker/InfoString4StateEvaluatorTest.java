package com.aquabasilea.domain.coursebooker;

import com.aquabasilea.domain.course.model.Course;
import com.aquabasilea.domain.coursebooker.model.state.CourseBookingState;
import com.aquabasilea.domain.coursebooker.config.AquabasileaCourseBookerConfig;
import com.aquabasilea.application.i18n.TextResources;
import com.brugalibre.util.date.DateUtil;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

class InfoString4StateEvaluatorTest {

   private static final AquabasileaCourseBookerConfig CONFIG = mockAquabasileaCourseBookerConfig();

   @Test
   void getInfoString4StateInit() {

      // Given
      Course currentCourse = new Course();
      currentCourse.setCourseName("test");
      currentCourse.setCourseDate(LocalDateTime.now());

      // When
      String actualInfoString4State = new InfoString4StateEvaluator(CONFIG).getInfoString4State(CourseBookingState.INIT, currentCourse);

      // Then
      assertThat(actualInfoString4State, is(TextResources.INFO_TEXT_INIT));
   }

   @Test
   void getInfoString4StatePaused() {

      // Given
      Course currentCourse = new Course();
      currentCourse.setCourseName("test");
      currentCourse.setCourseDate(LocalDateTime.now());

      // When
      String actualInfoString4State = new InfoString4StateEvaluator(CONFIG).getInfoString4State(CourseBookingState.PAUSED, currentCourse);

      // Then
      assertThat(actualInfoString4State, is(TextResources.INFO_TEXT_APP_PAUSED));
   }

   @Test
   void getInfoString4StateIdleBeforeBooking() {

      // Given
      int min2StartEarlier = 50;
      String hour = "21";
      int min = 15;
      Course currentCourse = buildCourse("Kurs123", hour, String.valueOf(min));
      LocalDateTime courseDate = currentCourse.getCourseDate();
      LocalDateTime dryRunOrBookingDate = courseDate.minusMinutes(min2StartEarlier);
      String dryRunOrBookingDateAsString = DateUtil.toString(dryRunOrBookingDate, Locale.GERMAN);
      String expectedInfoString = String.format(TextResources.INFO_TEXT_IDLE_BEFORE_BOOKING, currentCourse.getCourseName(), DateUtil.toString(courseDate, Locale.GERMAN), dryRunOrBookingDateAsString);
      AquabasileaCourseBookerConfig config = mockAquabasileaCourseBookerConfig();
      when(config.getDurationToStartBookerEarlier()).thenReturn(Duration.ofMinutes(min2StartEarlier));

      // When
      String actualInfoString4State = new InfoString4StateEvaluator(config).getInfoString4State(CourseBookingState.IDLE_BEFORE_BOOKING, currentCourse);

      // Then
      assertThat(actualInfoString4State, is(expectedInfoString));
      verify(config.refresh());
   }

   @Test
   void getInfoString4StateIdleBeforeDryRun() {

      // Given
      int min2StartEarlier = 50;
      String hour = "21";
      int min = 55;
      Course currentCourse = buildCourse("Kurs123", hour, String.valueOf(min));
      LocalDateTime courseDate = currentCourse.getCourseDate();
      LocalDateTime dryRunOrBookingDate = courseDate.minusMinutes(min2StartEarlier);
      String dryRunOrBookingDateAsString = DateUtil.toString(dryRunOrBookingDate, Locale.GERMAN);
      String expectedInfoString = String.format(TextResources.INFO_TEXT_IDLE_BEFORE_DRY_RUN, currentCourse.getCourseName(), DateUtil.toString(courseDate, Locale.GERMAN), dryRunOrBookingDateAsString);
      AquabasileaCourseBookerConfig config = mockAquabasileaCourseBookerConfig();
      when(config.getDurationToStartDryRunEarlier()).thenReturn(Duration.ofMinutes(min2StartEarlier));

      // When
      String actualInfoString4State = new InfoString4StateEvaluator(config).getInfoString4State(CourseBookingState.IDLE_BEFORE_DRY_RUN, currentCourse);

      // Then
      assertThat(actualInfoString4State, is(expectedInfoString));
   }

   @Test
   void getInfoString4StateBooking() {

      // Given
      Course currentCourse = new Course();
      String courseName = "test";
      currentCourse.setCourseName(courseName);
      currentCourse.setCourseDate(LocalDateTime.now());

      // When
      String actualInfoString4State = new InfoString4StateEvaluator(CONFIG).getInfoString4State(CourseBookingState.BOOKING, currentCourse);

      // Then
      assertThat(actualInfoString4State, is(String.format(TextResources.INFO_TEXT_BOOKING_COURSE, courseName)));
   }

   @Test
   void getInfoString4StateDryRun() {

      // Given
      Course currentCourse = new Course();
      String courseName = "test";
      currentCourse.setCourseName(courseName);
      currentCourse.setCourseDate(LocalDateTime.now());

      // When
      String actualInfoString4State = new InfoString4StateEvaluator(CONFIG).getInfoString4State(CourseBookingState.BOOKING_DRY_RUN, currentCourse);

      // Then
      assertThat(actualInfoString4State, is(String.format(TextResources.INFO_TEXT_BOOKING_COURSE_DRY_RUN, courseName)));
   }

   private static Course buildCourse(String courseName, String hour, String min) {
      Course currentCourse = new Course();
      currentCourse.setCourseName(courseName);
      currentCourse.setCourseDate(LocalDateTime.of(LocalDate.now(), LocalTime.of(Integer.parseInt(hour), Integer.parseInt(min))));
      return currentCourse;
   }

   private static AquabasileaCourseBookerConfig mockAquabasileaCourseBookerConfig() {
      AquabasileaCourseBookerConfig aquabasileaCourseBookerConfig = mock(AquabasileaCourseBookerConfig.class);
      when(aquabasileaCourseBookerConfig.refresh()).thenReturn(aquabasileaCourseBookerConfig);
      return aquabasileaCourseBookerConfig;
   }
}