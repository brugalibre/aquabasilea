package com.aquabasilea.domain.coursebooker.model.booking;

import com.aquabasilea.domain.courselocation.model.CourseLocation;

import java.time.LocalDateTime;

public record CourseBookDetails(String courseName, String courseInstructor, LocalDateTime courseDate,
                                CourseLocation courseLocation) {
   // no-op
}
