package com.zeiterfassung.web.aquabasilea.selectcourse.result;

import java.util.List;

public class CourseBookingEndResult {
   private List<String> errors;
   private Exception exception;
   private String courseName;
   private CourseClickedResult courseClickedResult;

   private CourseBookingEndResult(String courseName, CourseClickedResult courseClickedResult, List<String> errors, Exception exception) {
      this.errors = errors;
      this.exception = exception;
      this.courseName = courseName;
      this.courseClickedResult = courseClickedResult;
   }

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

   @Override
   public String toString() {
      return "CourseBookingEndResult{" +
              "errors=" + errors +
              ", exception=" + exception +
              ", courseName='" + courseName + '\'' +
              ", courseClickedResult=" + courseClickedResult +
              '}';
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
