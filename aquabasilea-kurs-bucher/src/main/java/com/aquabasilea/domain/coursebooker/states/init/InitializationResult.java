package com.aquabasilea.domain.coursebooker.states.init;

import com.aquabasilea.domain.course.Course;
import com.aquabasilea.domain.course.WeeklyCourses;
import com.aquabasilea.domain.coursebooker.states.CourseBookingState;

import java.time.Duration;

public class InitializationResult {
   private final CourseBookingState nextCourseBookingState;
   private final Course currentCourse;
   private final Duration durationUtilDryRunOrBookingBegin;
   private final WeeklyCourses updatedWeeklyCourses;
   private final int amountOfDaysPrior;

   public static InitializationResult pause(WeeklyCourses updatedWeeklyCourses) {
      return new InitializationResult(null, 0, CourseBookingState.PAUSED, 0, updatedWeeklyCourses);
   }

   public static InitializationResult dryRunInitializationResult(InitializationResult idleBeforeBookingResult, long durationUtilDryRunOrBookingBegin) {
      return new InitializationResult(idleBeforeBookingResult.getCurrentCourse(), durationUtilDryRunOrBookingBegin,
              CourseBookingState.IDLE_BEFORE_DRY_RUN, idleBeforeBookingResult.amountOfDaysPrior, idleBeforeBookingResult.updatedWeeklyCourses);
   }

   public static InitializationResult bookingInitializationResult(Course currentCourse, long durationUtilDryRunOrBookingBegin, int amountOfDaysPrior, WeeklyCourses updatedWeeklyCourses) {
      return new InitializationResult(currentCourse, durationUtilDryRunOrBookingBegin, CourseBookingState.IDLE_BEFORE_BOOKING, amountOfDaysPrior, updatedWeeklyCourses);
   }

   private InitializationResult (Course currentCourse, long durationUtilDryRunOrBookingBegin, CourseBookingState nextCourseBookingState,
                               int amountOfDaysPrior, WeeklyCourses updatedWeeklyCourses) {
      this.currentCourse = currentCourse;
      this.nextCourseBookingState = nextCourseBookingState;
      this.amountOfDaysPrior = amountOfDaysPrior;
      this.updatedWeeklyCourses = updatedWeeklyCourses;
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

   public WeeklyCourses getUpdatedWeeklyCourses() {
      return updatedWeeklyCourses;
   }
}
