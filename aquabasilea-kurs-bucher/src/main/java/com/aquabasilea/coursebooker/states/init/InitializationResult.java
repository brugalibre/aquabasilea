package com.aquabasilea.coursebooker.states.init;

import com.aquabasilea.course.Course;
import com.aquabasilea.coursebooker.states.CourseBookingState;

public class InitializationResult {
   private CourseBookingState nextCourseBookingState;
   private Course currentCourse;
   private long timeUtilDryRunOrBookingBegin;
   int amountOfDaysPrior;

   public static InitializationResult stop() {
      return new InitializationResult(null, 0, CourseBookingState.STOP, 0);
   }

   public InitializationResult(Course currentCourse, long timeUtilDryRunOrBookingBegin, CourseBookingState nextCourseBookingState, int amountOfDaysPrior) {
      this.currentCourse = currentCourse;
      this.nextCourseBookingState = nextCourseBookingState;
      this.amountOfDaysPrior = amountOfDaysPrior;
      this.timeUtilDryRunOrBookingBegin = timeUtilDryRunOrBookingBegin;
   }

   public CourseBookingState getNextCourseBookingState() {
      return nextCourseBookingState;
   }

   public Course getCurrentCourse() {
      return currentCourse;
   }

   public long getTimeUtilDryRunOrBookingBegin() {
      return timeUtilDryRunOrBookingBegin;
   }

   public int getAmountOfDaysPrior() {
      return amountOfDaysPrior;
   }
}
