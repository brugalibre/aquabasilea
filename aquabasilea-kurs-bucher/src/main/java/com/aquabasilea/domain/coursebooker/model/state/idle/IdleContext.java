package com.aquabasilea.domain.coursebooker.model.state.idle;

import com.aquabasilea.domain.coursebooker.AquabasileaCourseBooker;
import com.aquabasilea.domain.coursebooker.model.state.CourseBookingState;

import java.time.Duration;

public record IdleContext(Duration idleTime, CourseBookingState courseBookingState,
                          AquabasileaCourseBooker.UserContext userContext, boolean isPaused) {
    public static IdleContext of(Duration duration2StayIdle, CourseBookingState courseBookingState, AquabasileaCourseBooker.UserContext userContext) {
        return new IdleContext(duration2StayIdle, courseBookingState, userContext, false);
    }

    public static IdleContext isPaused(AquabasileaCourseBooker.UserContext userContext) {
        return new IdleContext(Duration.ofDays(365), CourseBookingState.PAUSED, userContext, true);
    }
}
