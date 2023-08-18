package com.aquabasilea.domain.coursebooker.states;

import com.aquabasilea.domain.course.Course;
import com.aquabasilea.domain.coursebooker.AquabasileaCourseBooker;

public enum CourseBookingState {
   /**
    * In this state, the {@link AquabasileaCourseBooker} initializes itself and the next course
    * This also defines the next state: is there time left for a dry run or are we going directly
    * for the actual booking of the course?
    */
   INIT,

   /**
    * This state indicates that the {@link AquabasileaCourseBooker} is refreshing its {@link Course}s
    */
   REFRESH_COURSES,

   /**
    * In this state, the {@link AquabasileaCourseBooker} is waiting until the next dry-run
    */
   IDLE_BEFORE_DRY_RUN,

   /**
    * In this state, the {@link AquabasileaCourseBooker} does the actual course booking
    */
   BOOKING_DRY_RUN,

   /**
    * In this state, the {@link AquabasileaCourseBooker} is waiting after the dry-run and before the next booking
    */
   IDLE_BEFORE_BOOKING,

   /**
    * In this state, the {@link AquabasileaCourseBooker} acts like he would book a course in
    * order to verify if the website hasn't changed
    */
   BOOKING,

   /**
    * In this state the {@link AquabasileaCourseBooker} is going to terminate
    */
   STOP,

   /**
    * In this state, the {@link AquabasileaCourseBooker} is paused and does nothing until it's resumed
    */
   PAUSED;

   public CourseBookingState next() {
      return getNextState(this);
   }

   private static CourseBookingState getNextState(CourseBookingState courseBookingState) {
      switch (courseBookingState) {
         case IDLE_BEFORE_BOOKING:
            return BOOKING;
         case IDLE_BEFORE_DRY_RUN:
            return BOOKING_DRY_RUN;
         case BOOKING:
         case BOOKING_DRY_RUN, REFRESH_COURSES, PAUSED:
            return INIT;
      }
      throw new IllegalStateException("Unknown state '" + courseBookingState + "'!");
   }
}
