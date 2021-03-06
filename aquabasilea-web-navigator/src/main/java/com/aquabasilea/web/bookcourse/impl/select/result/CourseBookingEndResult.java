package com.aquabasilea.web.bookcourse.impl.select.result;

import java.util.List;

public record CourseBookingEndResult(String courseName, CourseClickedResult courseClickedResult, List<String> errors,
                                     Exception exception) {

   public List<String> getErrors() {
      return errors;
   }

   public CourseClickedResult getCourseClickedResult() {
      return courseClickedResult;
   }

   public String getCourseName() {
      return courseName;
   }

   public Exception getException() {
      return exception;
   }

   public static class CourseBookingEndResultBuilder {
      private List<String> errors;
      private String courseName;
      private CourseClickedResult courseClickedResult;
      private Exception exception;

      private CourseBookingEndResultBuilder() {
         // private
      }

      public CourseBookingEndResultBuilder withErrors(List<String> errors) {
         this.errors = errors;
         return this;
      }

      public CourseBookingEndResultBuilder withCourseName(String courseName) {
         this.courseName = courseName;
         return this;
      }

      public CourseBookingEndResultBuilder withException(Exception exception) {
         this.exception = exception;
         return this;
      }

      public CourseBookingEndResultBuilder withCourseClickedResult(CourseClickedResult courseClickedResult) {
         this.courseClickedResult = courseClickedResult;
         return this;
      }

      public static CourseBookingEndResultBuilder builder() {
         return new CourseBookingEndResultBuilder();
      }

      public CourseBookingEndResult build() {
         return new CourseBookingEndResult(courseName, courseClickedResult, errors, exception);
      }
   }
}
