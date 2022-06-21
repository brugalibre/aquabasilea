package com.aquabasilea.web.extractcourses.model;

import com.aquabasilea.web.model.CourseLocation;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A {@link AquabasileaCourse} defines a guided course which can be booked at a certain day and a certain time
 */
public record AquabasileaCourse(LocalDateTime courseDate, CourseLocation courseLocation, String courseName) {
   // no-op
}

