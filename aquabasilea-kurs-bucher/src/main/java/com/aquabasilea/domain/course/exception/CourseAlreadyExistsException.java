package com.aquabasilea.domain.course.exception;

public class CourseAlreadyExistsException extends RuntimeException {
   public CourseAlreadyExistsException(String msg) {
      super(msg);
   }
}
