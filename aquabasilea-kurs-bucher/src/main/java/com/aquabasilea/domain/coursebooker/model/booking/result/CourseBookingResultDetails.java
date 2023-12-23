package com.aquabasilea.domain.coursebooker.model.booking.result;

import com.aquabasilea.domain.course.model.Course;

/**
 * The {@link CourseBookingResultDetails} contains the actual result as a {@link CourseBookResult}
 * as well as the name of the {@link Course} and an optional error-message
 */
public interface CourseBookingResultDetails {

   /**
    * @return the name of the {@link Course}
    */
   String getCourseName();

   /**
    * @return the actual result as a {@link CourseBookResult}
    */
   CourseBookResult getCourseBookResult();

   /**
    * @return an optional error message
    */
   String getErrorMessage();
}
