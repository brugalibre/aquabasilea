package com.aquabasilea.model.course;

import com.aquabasilea.util.DateUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LocalDateTimeBuilderTest {

   @Test
   void testGetLocalDateTimeNewYearEveAndCourseDayOfWeekIsInFuture() {
      // Given
      int expectedYear = 2022;
      int expectedMonth = 1;
      DayOfWeek dayOfTheWeek = DayOfWeek.SATURDAY;
      String timeOfTheDay = "18:00";
      LocalDateTime refDate = LocalDateTime.of(2021, 12, 31, 10, 15);// Friday

      // When
      LocalDateTime actualCreateLDTime = LocalDateTimeBuilder.createLocalDateTimeWithReferenceDate(refDate, dayOfTheWeek, timeOfTheDay);

      // Then
      assertThat(actualCreateLDTime.getYear(), is(expectedYear));
      assertThat(actualCreateLDTime.getMonthValue(), is(expectedMonth));
      assertThat(actualCreateLDTime.getDayOfWeek(), is(dayOfTheWeek));
   }

   @Test
   void testGetLocalDateTimeWithReferenceDateIsNow() {
      // Given
      LocalDateTime refDate = LocalDateTime.now();
      String timeOfTheDay = DateUtil.getTimeAsString(refDate);

      // When
      LocalDateTime actualCreateLDTime = LocalDateTimeBuilder.createLocalDateTimeWithReferenceDate(refDate, refDate.getDayOfWeek(), timeOfTheDay);

      // Then
      assertThat(actualCreateLDTime.getYear(), is(refDate.getYear()));
      assertThat(actualCreateLDTime.getMonthValue(), is(refDate.getMonthValue()));
      assertThat(actualCreateLDTime.getDayOfWeek(), is(refDate.getDayOfWeek()));
      assertThat(actualCreateLDTime.getDayOfMonth(), is(refDate.getDayOfMonth() + 7));
   }

   @Test
   void testGetLocalDateDayNearEndOfMonthAndCourseDayOfWeekLaysInNextMonth() {
      // Given
      int expectedYear = 2022;
      int expectedMonth = 4;
      int expectedDayOfWeek = DayOfWeek.SUNDAY.getValue();
      DayOfWeek dayOfTheWeek = DayOfWeek.SUNDAY;
      String timeOfTheDay = "18:00";
      LocalDateTime refDate = LocalDateTime.of(2022, 3, 28, 10, 15);// Monday

      // When
      LocalDateTime actualCreateLDTime = LocalDateTimeBuilder.createLocalDateTimeWithReferenceDate(refDate, dayOfTheWeek, timeOfTheDay);

      // Then
      assertThat(actualCreateLDTime.getYear(), is(expectedYear));
      assertThat(actualCreateLDTime.getMonthValue(), is(expectedMonth));
      assertThat(actualCreateLDTime.getDayOfWeek(), is(dayOfTheWeek));
      assertThat(actualCreateLDTime.getDayOfWeek().getValue(), is(expectedDayOfWeek));
   }

   @Test
   void testGetLocalDateDayNewDayOfWeekIsSmallerThanCurrent() {
      // Given
      int currentYear = 2022;
      int currentMonth = 3;
      int expectedDayOfWeek = DayOfWeek.MONDAY.getValue();

      DayOfWeek dayOfTheWeek = DayOfWeek.MONDAY;
      String timeOfTheDay = "18:00";
      LocalDateTime refDate = LocalDateTime.of(currentYear, currentMonth, 26, 10, 15);// Saturday

      // When
      LocalDateTime actualCreateLDTime = LocalDateTimeBuilder.createLocalDateTimeWithReferenceDate(refDate, dayOfTheWeek, timeOfTheDay);

      // Then
      assertThat(actualCreateLDTime.getYear(), is(currentYear));
      assertThat(actualCreateLDTime.getMonthValue(), is(currentMonth));
      assertThat(actualCreateLDTime.getDayOfWeek(), is(dayOfTheWeek));
      assertThat(actualCreateLDTime.getDayOfWeek().getValue(), is(expectedDayOfWeek));
   }

   @Test
   void testGetLocalDateTimeCourseTimeIsOneMinuteBeforeNow() {
      // Given
      int currentMonth = 3;
      DayOfWeek dayOfTheWeek = DayOfWeek.TUESDAY;
      int expectedDayOfWeek = dayOfTheWeek.getValue();
      int refDayOfTheMonth = 1; // Tuesday
      int expectedDayOfTheMonth = refDayOfTheMonth + 7;
      int hour = 10;
      int minute = 15;

      String timeOfTheDay = hour + ":" + (minute - 1);
      LocalDateTime refDate = LocalDateTime.of(2022, Month.MARCH, refDayOfTheMonth, hour, minute);// S

      // When
      LocalDateTime actualCreateLDTime = LocalDateTimeBuilder.createLocalDateTimeWithReferenceDate(refDate, dayOfTheWeek, timeOfTheDay);

      // Then
      assertThat(actualCreateLDTime.getYear(), is(2022));
      assertThat(actualCreateLDTime.getMonthValue(), is(currentMonth));
      assertThat(actualCreateLDTime.getDayOfWeek(), is(dayOfTheWeek));
      assertThat(actualCreateLDTime.getDayOfWeek().getValue(), is(expectedDayOfWeek));
      assertThat(actualCreateLDTime.getDayOfMonth(), is(expectedDayOfTheMonth));
   }

   @Test
   void testGetLocalDateTimeCourseTimeIsOneHourBeforeNow() {
      // Given
      int currentMonth = 3;
      DayOfWeek dayOfTheWeek = DayOfWeek.TUESDAY;
      int expectedDayOfWeek = dayOfTheWeek.getValue();
      int refDayOfTheMonth = 1; // Tuesday
      int expectedDayOfTheMonth = refDayOfTheMonth + 7;
      int hour = 10;
      int minute = 15;

      String timeOfTheDay = (hour -1) + ":" + minute;
      LocalDateTime refDate = LocalDateTime.of(2022, Month.MARCH, refDayOfTheMonth, hour, minute);// S

      // When
      LocalDateTime actualCreateLDTime = LocalDateTimeBuilder.createLocalDateTimeWithReferenceDate(refDate, dayOfTheWeek, timeOfTheDay);

      // Then
      assertThat(actualCreateLDTime.getYear(), is(2022));
      assertThat(actualCreateLDTime.getMonthValue(), is(currentMonth));
      assertThat(actualCreateLDTime.getDayOfWeek(), is(dayOfTheWeek));
      assertThat(actualCreateLDTime.getDayOfWeek().getValue(), is(expectedDayOfWeek));
      assertThat(actualCreateLDTime.getDayOfMonth(), is(expectedDayOfTheMonth));
   }

   @Test
   void testGetLocalDateTimeNewYearAndCourseDayOfWeekIsNotInFuture() {
      // Given
      int expectedYear = 2021;
      int expectedMonth = 12;
      DayOfWeek dayOfTheWeek = DayOfWeek.SATURDAY;
      String timeOfTheDay = "8:00";
      LocalDateTime refDate = LocalDateTime.of(2021, 12, 15, 10, 15);// Wednesday

      // When
      LocalDateTime actualCreateLDTime = LocalDateTimeBuilder.createLocalDateTimeWithReferenceDate(refDate, dayOfTheWeek, timeOfTheDay);

      // Then
      assertThat(actualCreateLDTime.getYear(), is(expectedYear));
      assertThat(actualCreateLDTime.getMonthValue(), is(expectedMonth));
      assertThat(actualCreateLDTime.getDayOfWeek(), is(dayOfTheWeek));
      assertThat(actualCreateLDTime.getHour(), is(8));
   }

   @Test
   void testGetLocalDateTimeInvalidTimeFormat() {
      // Given
      String timeOfTheDay = "888:00";
      LocalDateTime refDate = LocalDateTime.of(2021, 12, 15, 10, 15);

      // When
      Executable ex = () -> LocalDateTimeBuilder.createLocalDateTimeWithReferenceDate(refDate, DayOfWeek.SATURDAY, timeOfTheDay);

      // Then
      assertThrows(IllegalStateException.class, ex);
   }

   @Test
   void testGetLocalDateTimeInvalidDayOfWeek() {
      // Given
      String timeOfTheDay = "8:00";
      LocalDateTime refDate = LocalDateTime.of(2021, 12, 15, 10, 15);

      // When
      Executable ex = () -> LocalDateTimeBuilder.createLocalDateTimeWithReferenceDate(refDate, null, timeOfTheDay);

      // Then
      assertThrows(NullPointerException.class, ex);
   }

}