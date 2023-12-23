package com.aquabasilea.domain.statistics.service;

import com.aquabasilea.domain.coursebooker.model.booking.result.CourseBookResult;
import com.aquabasilea.domain.coursebooker.model.booking.result.CourseBookingResultDetails;
import com.aquabasilea.domain.coursebooker.model.state.CourseBookingState;
import com.aquabasilea.domain.coursebooker.states.booking.consumer.ConsumerUser;
import com.aquabasilea.domain.coursebooker.states.booking.consumer.CourseBookingEndResultConsumer;
import com.aquabasilea.domain.statistics.model.Statistics;
import com.aquabasilea.service.statistics.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link BookingStatisticsUpdater} updates the {@link Statistics} after a booking was completed
 */
public class BookingStatisticsUpdater implements CourseBookingEndResultConsumer {
   private static final Logger LOG = LoggerFactory.getLogger(BookingStatisticsUpdater.class);
   private final StatisticsService statisticsService;

   public BookingStatisticsUpdater(StatisticsService statisticsService) {
      this.statisticsService = statisticsService;
   }

   @Override
   public void consumeResult(ConsumerUser consumerUser, CourseBookingResultDetails courseBookingResultDetails, CourseBookingState courseBookingState) {
      if (courseBookingState == CourseBookingState.BOOKING) {
         handleCourseBookingStateBooking(consumerUser, courseBookingResultDetails);
      }
   }

   private void handleCourseBookingStateBooking(ConsumerUser consumerUser, CourseBookingResultDetails courseBookingResultDetails) {
      if (courseBookingResultDetails.getCourseBookResult() == CourseBookResult.BOOKED) {
         LOG.info("Increment successful bookings");
         this.statisticsService.incrementSuccessfulBookings(consumerUser.userId());
      } else if (courseBookingResultDetails.getCourseBookResult() != CourseBookResult.BOOKING_SKIPPED) {
         LOG.warn("Increment failed bookings");
         this.statisticsService.incrementFailedBookings(consumerUser.userId());
      } else {
         LOG.info("Neither failed nor successful bookings where incremented, click result: {}", courseBookingResultDetails.getCourseBookResult());
      }
   }
}
