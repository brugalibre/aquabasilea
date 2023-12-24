package com.aquabasilea.rest.model.course.weeklycourses;


import com.aquabasilea.rest.model.coursebooker.CourseLocationDto;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.time.LocalDateTime;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record CourseDto(String id, String courseName, String courseInstructor, String dayOfWeek, String timeOfTheDay,
                        LocalDateTime courseDate, CourseLocationDto courseLocationDto, boolean isPaused,
                        boolean hasCourseDef, boolean isCurrentCourse, String tooltipText, String bookingIdTac) {
// no-op
}


