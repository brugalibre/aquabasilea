package com.aquabasilea.statistics.model;

import com.aquabasilea.model.AbstractDomainModel;

import java.time.LocalDateTime;

import static java.util.Objects.isNull;

public class Statistics extends AbstractDomainModel {
   private LocalDateTime lastCourseDefUpdate;
   private LocalDateTime nextCourseDefUpdate;

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
}
