package com.aquabasilea.web.extractcourses.model;

import com.aquabasilea.web.model.CourseLocation;

import java.time.DayOfWeek;

/**
 * A {@link AquabasileaCourse} defines a guided course which can be booked at a certain day and a certain time
 */
public record AquabasileaCourse(DayOfWeek dayOfWeek, String timeOfTheDay, CourseLocation courseLocation, String courseName) {
   // no-op
}

