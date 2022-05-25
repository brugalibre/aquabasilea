package com.aquabasilea.course.aquabasilea;

import com.aquabasilea.course.CourseLocation;
import com.aquabasilea.course.user.Course;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * The {@link CourseDef} defines a bookable {@link Course}.
 * There is only one instance of a {@link CourseDef} but for one {@link CourseDef} there can be many {@link Course}s
 */
public record CourseDef(LocalDate courseDate, String timeOfTheDay, CourseLocation courseLocation, String courseName) {
   //
}
