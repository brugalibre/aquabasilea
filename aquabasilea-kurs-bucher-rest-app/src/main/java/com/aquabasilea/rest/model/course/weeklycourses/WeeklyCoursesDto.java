package com.aquabasilea.rest.model.course.weeklycourses;


import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record WeeklyCoursesDto(List<CourseDto> courseDtos) {
// no-op
}
