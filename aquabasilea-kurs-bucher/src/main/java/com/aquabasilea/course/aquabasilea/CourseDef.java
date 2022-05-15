package com.aquabasilea.course.aquabasilea;

import com.aquabasilea.course.CourseLocation;
import com.aquabasilea.course.user.Course;

import java.time.DayOfWeek;

/**
 * The {@link CourseDef} defines a bookable {@link Course}.
 * There is only one instance of a {@link CourseDef} but for one {@link CourseDef} there can be many {@link Course}s
 */
public record CourseDef(DayOfWeek dayOfWeek, String timeOfTheDay, CourseLocation courseLocation, String courseName) {
   //
}
