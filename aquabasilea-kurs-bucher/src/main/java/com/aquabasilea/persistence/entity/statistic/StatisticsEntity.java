package com.aquabasilea.persistence.entity.statistic;

import com.aquabasilea.persistence.entity.base.BaseEntity;
import org.springframework.lang.NonNull;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "statistics")
public class StatisticsEntity extends BaseEntity {

   private LocalDateTime lastCourseDefUpdate;
   private LocalDateTime nextCourseDefUpdate;
   private int bookingFailedCounter;
   private int bookingSuccessfulCounter;

   public StatisticsEntity(UUID id) {
      super(id);
   }

   public StatisticsEntity() {
      super(null);
   }

   @NonNull
   public LocalDateTime getLastCourseDefUpdate() {
      return lastCourseDefUpdate;
   }

   public void setLastCourseDefUpdate(@NonNull LocalDateTime lastCourseDefUpdate) {
      this.lastCourseDefUpdate = lastCourseDefUpdate;
   }

   @NonNull
   public LocalDateTime getNextCourseDefUpdate() {
      return nextCourseDefUpdate;
   }

   public void setNextCourseDefUpdate(@NonNull LocalDateTime nextCourseDefUpdate) {
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
