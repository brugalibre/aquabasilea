package com.aquabasilea.coursebooker.model.course.weeklycourses.exception;

public class CourseAlreadyExistsException extends RuntimeException {
   public CourseAlreadyExistsException(String msg) {
      super(msg);
   }
}
