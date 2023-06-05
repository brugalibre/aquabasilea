package com.aquabasilea.coursebooker.model.course.weeklycourses.exception;

import com.brugalibre.common.domain.exception.CommonDomainException;

public class NoCourseFoundException extends CommonDomainException {
   public NoCourseFoundException(String weeklyCoursesId, String courseId) {
      super("No course found with id {} in WeeklyCourse with id {}", courseId, weeklyCoursesId);
   }
}
