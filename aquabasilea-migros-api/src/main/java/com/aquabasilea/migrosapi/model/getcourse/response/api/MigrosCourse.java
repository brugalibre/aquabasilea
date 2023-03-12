package com.aquabasilea.migrosapi.model.getcourse.response.api;

import java.time.LocalDateTime;

/**
 * A {@link MigrosCourse} defines a guided course which can be booked at a certain day and a certain time
 */
public record MigrosCourse(LocalDateTime courseDate, String centerId, String courseName,
                           String courseInstructor) {
   // no-op
}

