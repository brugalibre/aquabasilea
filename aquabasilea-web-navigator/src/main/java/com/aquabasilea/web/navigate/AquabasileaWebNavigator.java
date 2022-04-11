package com.aquabasilea.web.navigate;

import com.aquabasilea.web.selectcourse.result.CourseBookingEndResult;

import java.time.DayOfWeek;

/**
 * The {@link AquabasileaWebNavigator} navigates the aquabasilea webpage in order to
 * select and book one of their sport courses
 */
public interface AquabasileaWebNavigator {
   /**
    * Selects and books a course for the given name at the given day of the week
    *
    * @param courseName the name of the course
    * @param dayOfWeek  the {@link DayOfWeek}
    * @return a {@link CourseBookingEndResult} with details about what happened
    */
   CourseBookingEndResult selectAndBookCourse(String courseName, DayOfWeek dayOfWeek);
}
