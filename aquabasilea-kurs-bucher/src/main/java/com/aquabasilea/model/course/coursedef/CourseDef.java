package com.aquabasilea.model.course.coursedef;

import com.aquabasilea.model.course.CourseLocation;
import com.aquabasilea.model.course.weeklycourses.Course;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * The {@link CourseDef} defines a bookable {@link Course}.
 * There is only one instance of a {@link CourseDef} but for one {@link CourseDef} there can be many {@link Course}s
 */
public record CourseDef(LocalDateTime courseDate, CourseLocation courseLocation, String courseName, String courseInstructor) {
   //
}
