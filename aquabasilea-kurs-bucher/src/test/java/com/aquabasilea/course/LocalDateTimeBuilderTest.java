package com.aquabasilea.course;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LocalDateTimeBuilderTest {

   @Test
   void testGetLocalDateTimeNewYearEveAndCourseDayOfWeekIsInFuture() {
      // Given
      int expectedYear = 2022;
      int expectedMonth = 1;
      String dayOfTheWeek = DayOfWeek.SATURDAY.getDisplayName(TextStyle.FULL, Locale.GERMAN);
      String timeOfTheDay = "18:00";
      LocalDateTime refDate = LocalDateTime.of(2021, 12, 31, 10, 15);// Friday

      // When
      LocalDateTime actualCreateLDTime = LocalDateTimeBuilder.getLocalDateTimeWithReferenceDate(refDate, dayOfTheWeek, timeOfTheDay);

      // Then
      assertThat(actualCreateLDTime.getYear(), is(expectedYear));
      assertThat(actualCreateLDTime.getMonthValue(), is(expectedMonth));
      assertThat(actualCreateLDTime.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMAN), is(dayOfTheWeek));
   }

   @Test
   void testGetLocalDateDayNearEndOfMonthAndCourseDayOfWeekLaysInNextMonth() {
      // Given
      int expectedYear = 2022;
      int expectedMonth = 4;
      int expectedDayOfWeek = DayOfWeek.SUNDAY.getValue();
      String dayOfTheWeek = DayOfWeek.SUNDAY.getDisplayName(TextStyle.FULL, Locale.GERMAN);
      String timeOfTheDay = "18:00";
      LocalDateTime refDate = LocalDateTime.of(2022, 3, 28, 10, 15);// Monday

      // When
      LocalDateTime actualCreateLDTime = LocalDateTimeBuilder.getLocalDateTimeWithReferenceDate(refDate, dayOfTheWeek, timeOfTheDay);

      // Then
      assertThat(actualCreateLDTime.getYear(), is(expectedYear));
      assertThat(actualCreateLDTime.getMonthValue(), is(expectedMonth));
      assertThat(actualCreateLDTime.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMAN), is(dayOfTheWeek));
      assertThat(actualCreateLDTime.getDayOfWeek().getValue(), is(expectedDayOfWeek));
   }

   @Test
   void testGetLocalDateDayNewDayOfWeekIsSmallerThanCurrent() {
      // Given
      int currentYear = 2022;
      int currentMonth = 3;
      int expectedDayOfWeek = DayOfWeek.MONDAY.getValue();

      String dayOfTheWeek = DayOfWeek.MONDAY.getDisplayName(TextStyle.FULL, Locale.GERMAN);
      String timeOfTheDay = "18:00";
      LocalDateTime refDate = LocalDateTime.of(currentYear, currentMonth, 26, 10, 15);// Saturday

      // When
      LocalDateTime actualCreateLDTime = LocalDateTimeBuilder.getLocalDateTimeWithReferenceDate(refDate, dayOfTheWeek, timeOfTheDay);

      // Then
      assertThat(actualCreateLDTime.getYear(), is(currentYear));
      assertThat(actualCreateLDTime.getMonthValue(), is(currentMonth));
      assertThat(actualCreateLDTime.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMAN), is(dayOfTheWeek));
      assertThat(actualCreateLDTime.getDayOfWeek().getValue(), is(expectedDayOfWeek));
   }

   @Test
   void testGetLocalDateTimeCourseTimeIsOneMinuteBeforeNow() {
      // Given
      int currentMonth = 3;
      DayOfWeek tuesday = DayOfWeek.TUESDAY;
      int expectedDayOfWeek = tuesday.getValue();
      int refDayOfTheMonth = 1; // Tuesday
      int expectedDayOfTheMonth = refDayOfTheMonth + 7;
      int hour = 10;
      int minute = 15;

      String dayOfTheWeek = tuesday.getDisplayName(TextStyle.FULL, Locale.GERMAN);
      String timeOfTheDay = hour + ":" + (minute - 1);
      LocalDateTime refDate = LocalDateTime.of(2022, Month.MARCH, refDayOfTheMonth, hour, minute);// S

      // When
      LocalDateTime actualCreateLDTime = LocalDateTimeBuilder.getLocalDateTimeWithReferenceDate(refDate, dayOfTheWeek, timeOfTheDay);

      // Then
      assertThat(actualCreateLDTime.getYear(), is(2022));
      assertThat(actualCreateLDTime.getMonthValue(), is(currentMonth));
      assertThat(actualCreateLDTime.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMAN), is(dayOfTheWeek));
      assertThat(actualCreateLDTime.getDayOfWeek().getValue(), is(expectedDayOfWeek));
      assertThat(actualCreateLDTime.getDayOfMonth(), is(expectedDayOfTheMonth));
   }
   @Test
   void testGetLocalDateTimeCourseTimeIsOneHourBeforeNow() {
      // Given
      int currentMonth = 3;
      DayOfWeek tuesday = DayOfWeek.TUESDAY;
      int expectedDayOfWeek = tuesday.getValue();
      int refDayOfTheMonth = 1; // Tuesday
      int expectedDayOfTheMonth = refDayOfTheMonth + 7;
      int hour = 10;
      int minute = 15;

      String dayOfTheWeek = tuesday.getDisplayName(TextStyle.FULL, Locale.GERMAN);
      String timeOfTheDay = (hour -1) + ":" + minute;
      LocalDateTime refDate = LocalDateTime.of(2022, Month.MARCH, refDayOfTheMonth, hour, minute);// S

      // When
      LocalDateTime actualCreateLDTime = LocalDateTimeBuilder.getLocalDateTimeWithReferenceDate(refDate, dayOfTheWeek, timeOfTheDay);

      // Then
      assertThat(actualCreateLDTime.getYear(), is(2022));
      assertThat(actualCreateLDTime.getMonthValue(), is(currentMonth));
      assertThat(actualCreateLDTime.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMAN), is(dayOfTheWeek));
      assertThat(actualCreateLDTime.getDayOfWeek().getValue(), is(expectedDayOfWeek));
      assertThat(actualCreateLDTime.getDayOfMonth(), is(expectedDayOfTheMonth));
   }

   @Test
   void testGetLocalDateTimeNewYearAndCourseDayOfWeekIsNotInFuture() {
      // Given
      int expectedYear = 2021;
      int expectedMonth = 12;
      String dayOfTheWeek = DayOfWeek.SATURDAY.getDisplayName(TextStyle.FULL, Locale.GERMAN);
      String timeOfTheDay = "8:00";
      LocalDateTime refDate = LocalDateTime.of(2021, 12, 15, 10, 15);// Wednesday

      // When
      LocalDateTime actualCreateLDTime = LocalDateTimeBuilder.getLocalDateTimeWithReferenceDate(refDate, dayOfTheWeek, timeOfTheDay);

      // Then
      assertThat(actualCreateLDTime.getYear(), is(expectedYear));
      assertThat(actualCreateLDTime.getMonthValue(), is(expectedMonth));
      assertThat(actualCreateLDTime.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMAN), is(dayOfTheWeek));
      assertThat(actualCreateLDTime.getHour(), is(8));
   }

   @Test
   void testGetLocalDateTimeInvalidTimeFormat() {
      // Given
      String timeOfTheDay = "888:00";
      LocalDateTime refDate = LocalDateTime.of(2021, 12, 15, 10, 15);

      // When
      Executable ex = () -> LocalDateTimeBuilder.getLocalDateTimeWithReferenceDate(refDate, DayOfWeek.SATURDAY.name(), timeOfTheDay);

      // Then
      assertThrows(IllegalStateException.class, ex);
   }

   @Test
   void testGetLocalDateTimeInvalidDayOfWeek() {
      // Given
      String timeOfTheDay = "8:00";
      LocalDateTime refDate = LocalDateTime.of(2021, 12, 15, 10, 15);

      // When
      Executable ex = () -> LocalDateTimeBuilder.getLocalDateTimeWithReferenceDate(refDate, "Dimanche", timeOfTheDay);

      // Then
      assertThrows(NullPointerException.class, ex);
   }

}