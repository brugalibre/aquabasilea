package com.aquabasilea.coursebooker.states.callback;

import com.aquabasilea.coursebooker.states.CourseBookingState;

@FunctionalInterface
public interface CourseBookingStateChangedHandler {

   /**
    * Is called as soon as the state of a {@link com.aquabasilea.coursebooker.AquabasileaCourseBooker} has changed
    *
    * @param courseBookingState the new state
    */
   void onCourseBookingStateChanged(CourseBookingState courseBookingState);
}
