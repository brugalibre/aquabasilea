package com.aquabasilea.domain.statistics.service;

import com.aquabasilea.domain.coursebooker.model.booking.result.CourseBookResult;
import com.aquabasilea.domain.coursebooker.model.booking.result.CourseBookingResultDetailsImpl;
import com.aquabasilea.domain.coursebooker.model.state.CourseBookingState;
import com.aquabasilea.domain.coursebooker.states.booking.consumer.ConsumerUser;
import com.aquabasilea.domain.statistics.model.Statistics;
import com.aquabasilea.domain.statistics.model.repository.StatisticsRepository;
import com.aquabasilea.service.statistics.StatisticsService;
import org.junit.jupiter.api.Test;

import java.time.Clock;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookingStatisticsUpdaterTest {
   private static final ConsumerUser CONSUMER_USER = ConsumerUser.of("123", "0791234567");
   public static final String COURSE_NAME = "courseName";

   @Test
   void testBookingSuccessful() {

      // Given
      StatisticsRepository statisticsRepository = getStatisticsRepository();
      StatisticsService statisticsService = new StatisticsService(statisticsRepository);
      BookingStatisticsUpdater bookingStatisticsUpdater = new BookingStatisticsUpdater(statisticsService);

      // When
      bookingStatisticsUpdater.consumeResult(CONSUMER_USER, CourseBookingResultDetailsImpl.of(CourseBookResult.BOOKED, COURSE_NAME),
              CourseBookingState.BOOKING);

      // Then
      Statistics statisticsDto = statisticsService.getStatisticsByUserId(CONSUMER_USER.userId());
      assertThat(statisticsDto.getBookingSuccessfulCounter(), is(1));
   }

   @Test
   void testBookingFailed() {

      // Given
      StatisticsRepository statisticsRepository = getStatisticsRepository();
      StatisticsService statisticsService = new StatisticsService(statisticsRepository);
      BookingStatisticsUpdater bookingStatisticsUpdater = new BookingStatisticsUpdater(statisticsService);

      // When
      bookingStatisticsUpdater.consumeResult(CONSUMER_USER, CourseBookingResultDetailsImpl.of(CourseBookResult.NOT_BOOKED_TECHNICAL_ERROR, COURSE_NAME)
              , CourseBookingState.BOOKING);

      // Then
      Statistics statisticsDto = statisticsService.getStatisticsByUserId(CONSUMER_USER.userId());
      assertThat(statisticsDto.getBookingSuccessfulCounter(), is(0));
      assertThat(statisticsDto.getBookingFailedCounter(), is(1));
   }

   @Test
   void testBookingSkipped() {

      // Given
      StatisticsRepository statisticsRepository = getStatisticsRepository();
      StatisticsService statisticsService = new StatisticsService(statisticsRepository);
      BookingStatisticsUpdater bookingStatisticsUpdater = new BookingStatisticsUpdater(statisticsService);

      // When
      bookingStatisticsUpdater.consumeResult(CONSUMER_USER, CourseBookingResultDetailsImpl.of(CourseBookResult.BOOKING_SKIPPED, COURSE_NAME),
              CourseBookingState.BOOKING);

      // Then
      Statistics statisticsDto = statisticsService.getStatisticsByUserId(CONSUMER_USER.userId());
      assertThat(statisticsDto.getBookingSuccessfulCounter(), is(0));
      assertThat(statisticsDto.getBookingFailedCounter(), is(0));
   }

   @Test
   void testBookingDryRunFailed() {

      // Given
      StatisticsRepository statisticsRepository = getStatisticsRepository();
      StatisticsService statisticsService = new StatisticsService(statisticsRepository);
      BookingStatisticsUpdater bookingStatisticsUpdater = new BookingStatisticsUpdater(statisticsService);

      // When
      bookingStatisticsUpdater.consumeResult(CONSUMER_USER, CourseBookingResultDetailsImpl.of(CourseBookResult.NOT_BOOKED_TECHNICAL_ERROR, COURSE_NAME),
              CourseBookingState.BOOKING_DRY_RUN);

      // Then
      Statistics statisticsDto = statisticsService.getStatisticsByUserId(CONSUMER_USER.userId());
      assertThat(statisticsDto.getBookingSuccessfulCounter(), is(0));
      assertThat(statisticsDto.getBookingFailedCounter(), is(0));
   }

   private static StatisticsRepository getStatisticsRepository() {
      StatisticsRepository statisticsRepository = mock(StatisticsRepository.class);
      when(statisticsRepository.getByUserId(CONSUMER_USER.userId())).thenReturn(new Statistics(CONSUMER_USER.userId(), Clock.systemDefaultZone()));
      return statisticsRepository;
   }

}