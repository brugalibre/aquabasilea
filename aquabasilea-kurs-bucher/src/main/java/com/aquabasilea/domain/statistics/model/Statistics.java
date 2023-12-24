package com.aquabasilea.domain.statistics.model;

import com.aquabasilea.domain.coursedef.update.CourseDefUpdaterScheduler;
import com.brugalibre.common.domain.model.AbstractDomainModel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;

import static java.util.Objects.isNull;

public class Statistics extends AbstractDomainModel {
   private final Clock clock;
   private String userId;
   private LocalDateTime lastCourseDefUpdate;
   private LocalDateTime nextCourseDefUpdate;
   private int bookingFailedCounter;
   private int bookingSuccessfulCounter;

   public Statistics() {
      this(null, Clock.systemDefaultZone());
   }

   public Statistics(String userId, Clock clock) {
      this.userId = userId;
      this.clock = clock;
   }

   /**
    * @return <code>true</code> if there was never a course-def update before or if it is too long ago. Otherwise returns <code>false</code>
    */
   public boolean needsCourseDefUpdate() {
      return isNull(this.lastCourseDefUpdate)
              || lastUpdateIsTooOld();
   }

   /**
    * @return the overall success rate calculated by the failed and successful bookings
    */
   public double getBookingSuccessRate() {
      int totalBookingCounter = getTotalBookingsCounter();
      if (totalBookingCounter == 0) {
         return 0;
      }
      BigDecimal bookingSuccessfulCounterBD = BigDecimal.valueOf(bookingSuccessfulCounter);
      BigDecimal totalBookingCounterBD = BigDecimal.valueOf(totalBookingCounter);
      return bookingSuccessfulCounterBD.divide(totalBookingCounterBD, 3, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue();
   }

   /**
    * The last update is too old when the difference in days is either greater than the update-cycle in days or when this
    * difference is negative
    */
   private boolean lastUpdateIsTooOld() {
      Duration courseDefUpdateCycle = CourseDefUpdaterScheduler.getCourseDefUpdateCycle();
      int daysBetweenLastUpdate = LocalDateTime.now(clock).getDayOfYear() - this.lastCourseDefUpdate.getDayOfYear();
      return daysBetweenLastUpdate < 0 || daysBetweenLastUpdate >= courseDefUpdateCycle.toDays();
   }

   public LocalDateTime getLastCourseDefUpdate() {
      return lastCourseDefUpdate;
   }

   public void setLastCourseDefUpdate(LocalDateTime lastCourseDefUpdate) {
      this.lastCourseDefUpdate = lastCourseDefUpdate;
   }

   public LocalDateTime getNextCourseDefUpdate() {
      return nextCourseDefUpdate;
   }

   public void setNextCourseDefUpdate(LocalDateTime nextCourseDefUpdate) {
      this.nextCourseDefUpdate = nextCourseDefUpdate;
   }

   public int getBookingFailedCounter() {
      return bookingFailedCounter;
   }

   public void setBookingFailedCounter(int bookingFailedCounter) {
      this.bookingFailedCounter = bookingFailedCounter;
   }

   public int getBookingSuccessfulCounter() {
      return bookingSuccessfulCounter;
   }

   /**
    * @return the total amount of bookings
    */
   public int getTotalBookingsCounter() {
      return bookingSuccessfulCounter + bookingFailedCounter;
   }

   public void setBookingSuccessfulCounter(int bookingSuccessfulCounter) {
      this.bookingSuccessfulCounter = bookingSuccessfulCounter;
   }

   public String getUserId() {
      return userId;
   }

   public void setUserId(String userId) {
      this.userId = userId;
   }
}
