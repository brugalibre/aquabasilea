package com.aquabasilea.domain.coursebooker.model.state.idle;

import com.aquabasilea.domain.coursebooker.model.state.CourseBookingState;

public record IdleStateResult(CourseBookingState nextState) {
    public static IdleStateResult of(CourseBookingState courseBookingState) {
        return new IdleStateResult(courseBookingState);
    }
}
