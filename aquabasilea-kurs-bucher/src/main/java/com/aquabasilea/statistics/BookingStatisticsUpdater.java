package com.aquabasilea.statistics;

import com.aquabasilea.coursebooker.consumer.CourseBookingEndResultConsumer;
import com.aquabasilea.coursebooker.states.CourseBookingState;
import com.aquabasilea.persistence.entity.statistic.StatisticsHelper;
import com.aquabasilea.statistics.model.Statistics;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseBookingEndResult;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseClickedResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link BookingStatisticsUpdater} updates the {@link Statistics} after a booking was completed
 */
public class BookingStatisticsUpdater implements CourseBookingEndResultConsumer {
   private static final Logger LOG = LoggerFactory.getLogger(BookingStatisticsUpdater.class);
   private final StatisticsHelper statisticsHelper;

   public BookingStatisticsUpdater(StatisticsHelper statisticsHelper) {
      this.statisticsHelper = statisticsHelper;
   }

   @Override
   public void consumeResult(CourseBookingEndResult courseBookingEndResult, CourseBookingState courseBookingState) {
      if (courseBookingState == CourseBookingState.BOOKING) {
         handleCourseBookingStateBooking(courseBookingEndResult);
      }
   }

   private void handleCourseBookingStateBooking(CourseBookingEndResult courseBookingEndResult) {
      if (courseBookingEndResult.getCourseClickedResult() == CourseClickedResult.COURSE_BOOKED) {
         LOG.info("Increment successful bookings");
         this.statisticsHelper.incrementSuccessfulBookings();
      } else if (courseBookingEndResult.getCourseClickedResult() != CourseClickedResult.COURSE_BOOKING_SKIPPED) {
         LOG.warn("Increment failed bookings");
         this.statisticsHelper.incrementFailedBookings();
      } else {
         LOG.info("Neither failed nor successful bookings where incremented, click result: {}", courseBookingEndResult.getCourseClickedResult());
      }
   }
}
