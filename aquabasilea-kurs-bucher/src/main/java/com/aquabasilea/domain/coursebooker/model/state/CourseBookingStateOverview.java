package com.aquabasilea.domain.coursebooker.model.state;

import com.aquabasilea.domain.coursebooker.states.CourseBookingState;

public record CourseBookingStateOverview(CourseBookingState courseBookingState, String msg) {
}
