package com.aquabasilea.coursebooker.states.init;

import com.aquabasilea.model.course.weeklycourses.Course;
import com.aquabasilea.coursebooker.states.CourseBookingState;

import java.time.Duration;

public class InitializationResult {
   private final CourseBookingState nextCourseBookingState;
   private final Course currentCourse;
   private final Duration durationUtilDryRunOrBookingBegin;
   int amountOfDaysPrior;

   public static InitializationResult pause() {
      return new InitializationResult(null, 0, CourseBookingState.PAUSED, 0);
   }

   public InitializationResult(Course currentCourse, long durationUtilDryRunOrBookingBegin, CourseBookingState nextCourseBookingState, int amountOfDaysPrior) {
      this.currentCourse = currentCourse;
      this.nextCourseBookingState = nextCourseBookingState;
      this.amountOfDaysPrior = amountOfDaysPrior;
      this.durationUtilDryRunOrBookingBegin = Duration.ofMillis(durationUtilDryRunOrBookingBegin);
   }

   public CourseBookingState getNextCourseBookingState() {
      return nextCourseBookingState;
   }

   public Course getCurrentCourse() {
      return currentCourse;
   }

   public Duration getDurationUtilDryRunOrBookingBegin() {
      return durationUtilDryRunOrBookingBegin;
   }

   public int getAmountOfDaysPrior() {
      return amountOfDaysPrior;
   }
}
