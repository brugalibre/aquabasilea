package com.aquabasilea.coursebooker.states.init;

import com.aquabasilea.model.course.weeklycourses.Course;
import com.aquabasilea.coursebooker.states.CourseBookingState;

public class InitializationResult {
   private final CourseBookingState nextCourseBookingState;
   private final Course currentCourse;
   private final long timeUtilDryRunOrBookingBegin;
   int amountOfDaysPrior;

   public static InitializationResult pause() {
      return new InitializationResult(null, 0, CourseBookingState.PAUSED, 0);
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
