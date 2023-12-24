package com.aquabasilea.domain.coursebooker.model.state.idle;

import com.aquabasilea.domain.coursebooker.model.state.CourseBookingState;

import java.time.Duration;

public record IdleContext(Duration idleTime, CourseBookingState courseBookingState, boolean isPaused) {
   public static IdleContext of(Duration duration2StayIdle, CourseBookingState courseBookingState) {
      return new IdleContext(duration2StayIdle, courseBookingState, false);
   }

   public static IdleContext getAsPaused() {
      return new IdleContext(Duration.ofDays(365), CourseBookingState.PAUSED, true);
   }
}
