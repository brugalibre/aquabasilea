package com.aquabasilea.web.extractcourses.model;

import java.time.LocalDateTime;

/**
 * A {@link AquabasileaCourse} defines a guided course which can be booked at a certain day and a certain time
 */
public record AquabasileaCourse(LocalDateTime courseDate, String courseLocation, String courseName,
                                String courseInstructor) {
   // no-op
}

