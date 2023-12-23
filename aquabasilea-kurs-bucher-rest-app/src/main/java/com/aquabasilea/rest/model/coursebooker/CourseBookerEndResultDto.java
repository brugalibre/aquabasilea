package com.aquabasilea.rest.model.coursebooker;

import com.aquabasilea.domain.coursebooker.model.booking.result.CourseBookResult;
import com.aquabasilea.domain.coursebooker.model.booking.result.CourseBookingResultDetails;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record CourseBookerEndResultDto(String courseName, CourseBookResult courseBookResult, boolean success,
                                       String errorMsg) {
   public static CourseBookerEndResultDto of(CourseBookingResultDetails courseBookingResultDetails) {
      return new CourseBookerEndResultDto(courseBookingResultDetails.getCourseName(), courseBookingResultDetails.getCourseBookResult(),
              courseBookingResultDetails.getErrorMessage() != null, courseBookingResultDetails.getErrorMessage());
   }
}
