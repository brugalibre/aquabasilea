package com.aquabasilea.model.statistics;

import com.aquabasilea.model.course.coursedef.update.CourseDefUpdaterScheduler;
import com.brugalibre.common.domain.model.AbstractDomainModel;
import java.time.Duration;
import java.time.LocalDateTime;

import static java.util.Objects.isNull;

public class Statistics extends AbstractDomainModel {
   private String userId;
   private LocalDateTime lastCourseDefUpdate;
   private LocalDateTime nextCourseDefUpdate;
   private int bookingFailedCounter;
   private int bookingSuccessfulCounter;

   public Statistics(String userId) {
      this.userId = userId;
   }

   /**
    * @return <code>true</code> if there was never a course-def update before or if it is too long ago. Otherwise returns <code>false</code>
    */
   public boolean needsCourseDefUpdate() {
      return isNull(this.lastCourseDefUpdate)
              || lastUpdateIsTooOld();
   }

   private boolean lastUpdateIsTooOld() {
      Duration courseDefUpdateCycle = CourseDefUpdaterScheduler.getCourseDefUpdateCycle();
      return (LocalDateTime.now().getDayOfYear() - this.lastCourseDefUpdate.getDayOfYear()) >= courseDefUpdateCycle.toDays();
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

   public String getUserId() {
      return userId;
   }

   public void setUserId(String userId) {
      this.userId = userId;
   }
}
