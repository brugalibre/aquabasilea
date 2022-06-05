package com.aquabasilea.model.course.exception;

public class CourseAlreadyExistsException extends RuntimeException {
   public CourseAlreadyExistsException(String msg) {
      super(msg);
   }
}
