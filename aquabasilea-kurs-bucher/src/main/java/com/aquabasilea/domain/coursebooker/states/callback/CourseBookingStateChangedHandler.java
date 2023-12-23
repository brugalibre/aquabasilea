package com.aquabasilea.domain.coursebooker.states.callback;

import com.aquabasilea.domain.coursebooker.model.state.CourseBookingState;
import com.aquabasilea.domain.coursebooker.AquabasileaCourseBooker;

@FunctionalInterface
public interface CourseBookingStateChangedHandler {

   /**
    * Is called as soon as the state of a {@link AquabasileaCourseBooker} has changed
    *
    * @param courseBookingState the new state
    */
   void onCourseBookingStateChanged(CourseBookingState courseBookingState);
}
