package com.aquabasilea.rest.model.statistic;

import com.aquabasilea.statistics.model.Statistics;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class StatisticsDtoTest {

   @Test
   void testGetStatisticsDto() {

      // Given
      Statistics statistics = new Statistics();
      statistics.setBookingFailedCounter(61);
      statistics.setBookingSuccessfulCounter(79);
      LocalDateTime lastCourseDefUpdate = LocalDateTime.of(2022, Month.APRIL, 1, 12, 15);
      statistics.setLastCourseDefUpdate(lastCourseDefUpdate);
      statistics.setNextCourseDefUpdate(lastCourseDefUpdate.plusDays(7));

      // When
      StatisticsDto statisticsDto = StatisticsDto.of(statistics, Locale.GERMAN, "1h, 1min");

      // Then
      assertThat(statisticsDto.bookingSuccessRate(), is(56.4));
      assertThat(statisticsDto.lastCourseDefUpdate(), is("01.04.2022, 12:15 Uhr"));
      assertThat(statisticsDto.nextCourseDefUpdate(), is("08.04.2022, 12:15 Uhr"));
   }

   @Test
   void testGetStatisticsDtoZeroBookings() {

      // Given
      Statistics statistics = new Statistics();

      // When
      StatisticsDto statisticsDto = StatisticsDto.of(statistics, Locale.GERMAN, "1h, 1min");

      // Then
      assertThat(statisticsDto.bookingSuccessRate(), is(0.0));
      assertThat(statisticsDto.lastCourseDefUpdate(), is(" - "));
   }
}