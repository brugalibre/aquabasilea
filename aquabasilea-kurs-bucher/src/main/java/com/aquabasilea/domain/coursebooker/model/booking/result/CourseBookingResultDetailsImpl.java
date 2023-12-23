package com.aquabasilea.domain.coursebooker.model.booking.result;


public record CourseBookingResultDetailsImpl(CourseBookResult courseBookResult, String courseName,
                                             String errorMessage) implements CourseBookingResultDetails {
   public static CourseBookingResultDetailsImpl of(CourseBookResult courseBookResult, String courseName, String errorMessage) {
      return new CourseBookingResultDetailsImpl(courseBookResult, courseName, errorMessage);
   }

   public static CourseBookingResultDetailsImpl of(CourseBookResult courseBookResult, String courseName) {
      return new CourseBookingResultDetailsImpl(courseBookResult, courseName, null);
   }

   @Override
   public String getCourseName() {
      return courseName;
   }

   @Override
   public CourseBookResult getCourseBookResult() {
      return courseBookResult;
   }

   @Override
   public String getErrorMessage() {
      return errorMessage;
   }
}
