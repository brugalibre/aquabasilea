package com.aquabasilea.web.bookcourse;

import com.aquabasilea.web.bookcourse.impl.select.result.CourseBookingEndResult;
import com.aquabasilea.web.bookcourse.model.CourseBookDetails;

import java.time.DayOfWeek;

/**
 * The {@link AquabasileaWebCourseBooker} navigates the aquabasilea webpage in order to
 * select and book one of their sport courses
 */
public interface AquabasileaWebCourseBooker {
   /**
    * Selects and books a course for the given name at the given day of the week
    *
    * @param courseBookDetails the {@link CourseBookDetails} which contains details like the name of the course,
    *                         the course location, day of the week and so on
    * @return a {@link CourseBookingEndResult} with details about what happened
    */
   CourseBookingEndResult selectAndBookCourse(CourseBookDetails courseBookDetails);
}
