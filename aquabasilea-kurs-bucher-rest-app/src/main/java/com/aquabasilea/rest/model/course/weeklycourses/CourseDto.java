package com.aquabasilea.rest.model.course.weeklycourses;


import com.aquabasilea.model.course.CourseLocation;
import com.aquabasilea.model.course.weeklycourses.Course;
import com.aquabasilea.model.course.weeklycourses.Course.CourseBuilder;
import com.aquabasilea.rest.model.CourseLocationDto;
import com.aquabasilea.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.UUID;

import static java.util.Objects.isNull;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record CourseDto(String id, String courseName, String dayOfWeek, String timeOfTheDay, LocalDateTime courseDate,
                        CourseLocationDto courseLocationDto, boolean isPaused, boolean hasCourseDef,
                        boolean isCurrentCourse) {

   public static Course map2Course(CourseDto courseDto) {
      String currentId = isNull(courseDto.id) ? UUID.randomUUID().toString() : courseDto.id;
      return CourseBuilder.builder()
              .withId(currentId)
              .withCourseName(courseDto.courseName())
              .withCourseDate(courseDto.courseDate)
              .withIsPaused(courseDto.isPaused())
              .withHasCourseDef(courseDto.hasCourseDef)
              .withCourseLocation(CourseLocation.valueOf(courseDto.courseLocationDto.courseLocationKey()))
              .build();
   }

   public static CourseDto of(Course course, boolean isCurrentCourse, Locale locale) {
      return new CourseDto(course.getId(), course.getCourseName(), course.getCourseDate().getDayOfWeek().getDisplayName(TextStyle.FULL, locale),
              DateUtil.getTimeAsString(course.getCourseDate()), course.getCourseDate(), CourseLocationDto.of(course.getCourseLocation()),
              course.getIsPaused(), course.getHasCourseDef(), isCurrentCourse);
   }
}


