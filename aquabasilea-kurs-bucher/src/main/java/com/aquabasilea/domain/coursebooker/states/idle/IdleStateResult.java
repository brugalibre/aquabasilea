package com.aquabasilea.domain.coursebooker.states.idle;

import com.aquabasilea.domain.coursebooker.states.CourseBookingState;

public record IdleStateResult(CourseBookingState nextState) {
    public static IdleStateResult of(CourseBookingState courseBookingState) {
        return new IdleStateResult(courseBookingState);
    }
}
