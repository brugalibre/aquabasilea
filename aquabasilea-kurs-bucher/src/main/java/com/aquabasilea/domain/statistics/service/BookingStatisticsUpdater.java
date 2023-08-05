package com.aquabasilea.domain.statistics.service;

import com.aquabasilea.domain.coursebooker.states.CourseBookingState;
import com.aquabasilea.domain.coursebooker.states.booking.consumer.ConsumerUser;
import com.aquabasilea.domain.coursebooker.states.booking.consumer.CourseBookingEndResultConsumer;
import com.aquabasilea.domain.statistics.model.Statistics;
import com.aquabasilea.service.statistics.StatisticsService;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseBookingEndResult;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseClickedResult;
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
   public void consumeResult(ConsumerUser consumerUser, CourseBookingEndResult courseBookingEndResult, CourseBookingState courseBookingState) {
      if (courseBookingState == CourseBookingState.BOOKING) {
         handleCourseBookingStateBooking(consumerUser, courseBookingEndResult);
      }
   }

   private void handleCourseBookingStateBooking(ConsumerUser consumerUser, CourseBookingEndResult courseBookingEndResult) {
      if (courseBookingEndResult.getCourseClickedResult() == CourseClickedResult.COURSE_BOOKED) {
         LOG.info("Increment successful bookings");
         this.statisticsService.incrementSuccessfulBookings(consumerUser.userId());
      } else if (courseBookingEndResult.getCourseClickedResult() != CourseClickedResult.COURSE_BOOKING_SKIPPED) {
         LOG.warn("Increment failed bookings");
         this.statisticsService.incrementFailedBookings(consumerUser.userId());
      } else {
         LOG.info("Neither failed nor successful bookings where incremented, click result: {}", courseBookingEndResult.getCourseClickedResult());
      }
   }
}
