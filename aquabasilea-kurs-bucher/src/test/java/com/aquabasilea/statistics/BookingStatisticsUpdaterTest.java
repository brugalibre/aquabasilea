package com.aquabasilea.statistics;

import com.aquabasilea.coursebooker.states.CourseBookingState;
import com.aquabasilea.persistence.entity.statistic.StatisticsHelper;
import com.aquabasilea.statistics.model.Statistics;
import com.aquabasilea.statistics.repository.StatisticsRepository;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseBookingEndResult.CourseBookingEndResultBuilder;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseClickedResult;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookingStatisticsUpdaterTest {

   @Test
   void testBookingSuccessful() {

      // Given
      StatisticsRepository statisticsRepository = getStatisticsRepository();
      StatisticsHelper statisticsHelper = new StatisticsHelper(statisticsRepository);
      BookingStatisticsUpdater bookingStatisticsUpdater = new BookingStatisticsUpdater(statisticsHelper);

      // When
      bookingStatisticsUpdater.consumeResult(CourseBookingEndResultBuilder.builder()
              .withCourseClickedResult(CourseClickedResult.COURSE_BOOKED)
              .build(), CourseBookingState.BOOKING);

      // Then
      Statistics statisticsDto = statisticsHelper.getStatisticsDto();
      assertThat(statisticsDto.getBookingSuccessfulCounter(), is(1));
   }

   @Test
   void testBookingFailed() {

      // Given
      StatisticsRepository statisticsRepository = getStatisticsRepository();
      StatisticsHelper statisticsHelper = new StatisticsHelper(statisticsRepository);
      BookingStatisticsUpdater bookingStatisticsUpdater = new BookingStatisticsUpdater(statisticsHelper);

      // When
      bookingStatisticsUpdater.consumeResult(CourseBookingEndResultBuilder.builder()
              .withCourseClickedResult(CourseClickedResult.COURSE_NOT_BOOKABLE)
              .build(), CourseBookingState.BOOKING);

      // Then
      Statistics statisticsDto = statisticsHelper.getStatisticsDto();
      assertThat(statisticsDto.getBookingSuccessfulCounter(), is(0));
      assertThat(statisticsDto.getBookingFailedCounter(), is(1));
   }

   @Test
   void testBookingSkipped() {

      // Given
      StatisticsRepository statisticsRepository = getStatisticsRepository();
      StatisticsHelper statisticsHelper = new StatisticsHelper(statisticsRepository);
      BookingStatisticsUpdater bookingStatisticsUpdater = new BookingStatisticsUpdater(statisticsHelper);

      // When
      bookingStatisticsUpdater.consumeResult(CourseBookingEndResultBuilder.builder()
              .withCourseClickedResult(CourseClickedResult.COURSE_BOOKING_SKIPPED)
              .build(), CourseBookingState.BOOKING);

      // Then
      Statistics statisticsDto = statisticsHelper.getStatisticsDto();
      assertThat(statisticsDto.getBookingSuccessfulCounter(), is(0));
      assertThat(statisticsDto.getBookingFailedCounter(), is(0));
   }

   @Test
   void testBookingDryRunFailed() {

      // Given
      StatisticsRepository statisticsRepository = getStatisticsRepository();
      StatisticsHelper statisticsHelper = new StatisticsHelper(statisticsRepository);
      BookingStatisticsUpdater bookingStatisticsUpdater = new BookingStatisticsUpdater(statisticsHelper);

      // When
      bookingStatisticsUpdater.consumeResult(CourseBookingEndResultBuilder.builder()
              .withCourseClickedResult(CourseClickedResult.COURSE_NOT_BOOKABLE)
              .build(), CourseBookingState.BOOKING_DRY_RUN);

      // Then
      Statistics statisticsDto = statisticsHelper.getStatisticsDto();
      assertThat(statisticsDto.getBookingSuccessfulCounter(), is(0));
      assertThat(statisticsDto.getBookingFailedCounter(), is(0));
   }

   @NotNull
   private StatisticsRepository getStatisticsRepository() {
      StatisticsRepository statisticsRepository = mock(StatisticsRepository.class);
      when(statisticsRepository.findFirstStatisticsDto()).thenReturn(new Statistics());
      return statisticsRepository;
   }

}