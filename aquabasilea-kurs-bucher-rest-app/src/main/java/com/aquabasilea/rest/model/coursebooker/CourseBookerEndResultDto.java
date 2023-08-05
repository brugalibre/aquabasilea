package com.aquabasilea.rest.model.coursebooker;

import com.aquabasilea.web.bookcourse.impl.select.result.CourseBookingEndResult;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseClickedResult;
import com.fasterxml.jackson.annotation.JsonAutoDetect;


@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record CourseBookerEndResultDto(String courseName, CourseClickedResult courseClickedResult, boolean success,
                                       String errorMsg) {
    public static CourseBookerEndResultDto of(CourseBookingEndResult courseBookingEndResult) {
        return new CourseBookerEndResultDto(courseBookingEndResult.courseName(), courseBookingEndResult.courseClickedResult(), courseBookingEndResult.exception() != null, getMessage(courseBookingEndResult));
    }

    private static String getMessage(CourseBookingEndResult courseBookingEndResult) {
        Exception exception = courseBookingEndResult.exception();
        return exception == null ? null : exception.getMessage();
    }
}
