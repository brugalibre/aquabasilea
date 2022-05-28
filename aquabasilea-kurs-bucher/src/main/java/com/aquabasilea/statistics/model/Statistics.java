package com.aquabasilea.statistics.model;

import com.aquabasilea.model.AbstractDomainModel;

import java.time.LocalDateTime;

import static java.util.Objects.isNull;

public class Statistics extends AbstractDomainModel {
   private LocalDateTime lastCourseDefUpdate;
   private LocalDateTime nextCourseDefUpdate;
   private int bookingFailedCounter;
   private int bookingSuccessfulCounter;

   /**
    * @return <code>true</code> if there was never a course-def update before or if it is too long ago. Otherwise returns <code>false</code>
    */
   public boolean needsCourseDefUpdate() {
      return isNull(this.lastCourseDefUpdate)
              || lastUpdateIsOlderThanAWeek();
   }

   private boolean lastUpdateIsOlderThanAWeek() {
      return (this.lastCourseDefUpdate.getDayOfYear() - LocalDateTime.now().getDayOfYear()) >= 7;
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

   public void setBookingSuccessfulCounter(int bookingSuccessfulCounter) {
      this.bookingSuccessfulCounter = bookingSuccessfulCounter;
   }
}
