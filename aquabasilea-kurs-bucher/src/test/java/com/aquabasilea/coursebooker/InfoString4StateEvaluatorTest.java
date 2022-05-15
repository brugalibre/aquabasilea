package com.aquabasilea.coursebooker;

import com.aquabasilea.course.user.Course;
import com.aquabasilea.coursebooker.config.AquabasileaCourseBookerConfig;
import com.aquabasilea.coursebooker.states.CourseBookingState;
import com.aquabasilea.i18n.TextResources;
import com.aquabasilea.util.DateUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InfoString4StateEvaluatorTest {

   private static Course currentCourse;

   @BeforeEach
   public void setUp() {
      currentCourse = buildCourse("test", "22", "00");
   }

   @Test
   void getInfoString4StateInit() {

      // Given
      Course currentCourse = new Course();
      currentCourse.setCourseName("test");
      currentCourse.setDayOfWeek(DayOfWeek.MONDAY.getDisplayName(TextStyle.FULL, Locale.GERMAN));

      // When
      String actualInfoString4State = new InfoString4StateEvaluator(null).getInfoString4State(CourseBookingState.INIT, currentCourse);

      // Then
      assertThat(actualInfoString4State, is(TextResources.INFO_TEXT_INIT));
   }

   @Test
   void getInfoString4StatePaused() {

      // Given
      Course currentCourse = new Course();
      currentCourse.setCourseName("test");
      currentCourse.setDayOfWeek(DayOfWeek.MONDAY.getDisplayName(TextStyle.FULL, Locale.GERMAN));

      // When
      String actualInfoString4State = new InfoString4StateEvaluator(null).getInfoString4State(CourseBookingState.PAUSED, currentCourse);

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
      String expectedInfoString = String.format(TextResources.INFO_TEXT_IDLE_BEFORE_BOOKING, currentCourse.getCourseName(), DateUtil.toString(currentCourse.getCourseDate(), Locale.GERMAN), 20 + ":" + 25);
      AquabasileaCourseBookerConfig config = mock(AquabasileaCourseBookerConfig.class);
      when(config.getDurationToStartBookerEarlier()).thenReturn(Duration.ofMinutes(min2StartEarlier));

      // When
      String actualInfoString4State = new InfoString4StateEvaluator(config).getInfoString4State(CourseBookingState.IDLE_BEFORE_BOOKING, currentCourse);

      // Then
      assertThat(actualInfoString4State, is(expectedInfoString));
   }

   @Test
   void getInfoString4StateIdleBeforeDryRun() {

      // Given
      int min2StartEarlier = 50;
      String hour = "21";
      int min = 55;
      Course currentCourse = buildCourse("Kurs123", hour, String.valueOf(min));
      String diff = (min - min2StartEarlier) < 10 ? "0" + (min - min2StartEarlier) : "" + (min - min2StartEarlier);
      String expectedInfoString = String.format(TextResources.INFO_TEXT_IDLE_BEFORE_DRY_RUN, currentCourse.getCourseName(), DateUtil.toString(currentCourse.getCourseDate(), Locale.GERMAN), hour + ":" + diff);
      AquabasileaCourseBookerConfig config = mock(AquabasileaCourseBookerConfig.class);
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
      currentCourse.setDayOfWeek(DayOfWeek.MONDAY.getDisplayName(TextStyle.FULL, Locale.GERMAN));

      // When
      String actualInfoString4State = new InfoString4StateEvaluator(null).getInfoString4State(CourseBookingState.BOOKING, currentCourse);

      // Then
      assertThat(actualInfoString4State, is(String.format(TextResources.INFO_TEXT_BOOKING_COURSE, courseName)));
   }

   @Test
   void getInfoString4StateDryRun() {

      // Given
      Course currentCourse = new Course();
      String courseName = "test";
      currentCourse.setCourseName(courseName);
      currentCourse.setDayOfWeek(DayOfWeek.MONDAY.getDisplayName(TextStyle.FULL, Locale.GERMAN));
      
      // When
      String actualInfoString4State = new InfoString4StateEvaluator(null).getInfoString4State(CourseBookingState.BOOKING_DRY_RUN, currentCourse);

      // Then
      assertThat(actualInfoString4State, is(String.format(TextResources.INFO_TEXT_BOOKING_COURSE_DRY_RUN, courseName)));
   }

   private static Course buildCourse(String courseName, String hour, String min) {
      Course currentCourse = new Course();
      currentCourse.setCourseName(courseName);
      currentCourse.setTimeOfTheDay(hour + ":" + min);
      currentCourse.setDayOfWeek(LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMAN));
      return currentCourse;
   }
}