package com.aquabasilea.rest.model.statistic;

import com.aquabasilea.domain.statistics.model.Statistics;
import com.aquabasilea.domain.statistics.model.StatisticsOverview;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class StatisticsDtoTest {

   @Test
   void testGetStatisticsDto() {

      // Given
      Statistics statistics = new Statistics("123", Clock.systemDefaultZone());
      statistics.setBookingFailedCounter(61);
      statistics.setBookingSuccessfulCounter(79);
      StatisticsOverview statisticsOverview = new StatisticsOverview(statistics, statistics.getTotalBookingsCounter(), statistics.getBookingSuccessRate());
      LocalDateTime lastCourseDefUpdate = LocalDateTime.of(2022, Month.APRIL, 1, 12, 15);
      statistics.setLastCourseDefUpdate(lastCourseDefUpdate);
      statistics.setNextCourseDefUpdate(lastCourseDefUpdate.plusDays(7));

      // When
      StatisticsDto statisticsDto = StatisticsDto.of(statisticsOverview, Locale.GERMAN, "1h, 1min");

      // Then
      assertThat(statisticsDto.bookingSuccessRate(), is(56.4));
      assertThat(statisticsDto.lastCourseDefUpdate(), is("01.04.2022, 12:15 Uhr"));
      assertThat(statisticsDto.nextCourseDefUpdate(), is("08.04.2022, 12:15 Uhr"));
   }

   @Test
   void testGetStatisticsDtoZeroBookings() {

      // Given
      Statistics statistics = new Statistics("1234", Clock.systemDefaultZone());
      StatisticsOverview statisticsOverview = new StatisticsOverview(statistics, 0, 0);

      // When
      StatisticsDto statisticsDto = StatisticsDto.of(statisticsOverview, Locale.GERMAN, "1h, 1min");

      // Then
      assertThat(statisticsDto.bookingSuccessRate(), is(0.0));
      assertThat(statisticsDto.lastCourseDefUpdate(), is(" - "));
   }
}