package com.aquabasilea.rest.model.course.user;


import com.aquabasilea.course.CourseLocation;
import com.aquabasilea.course.user.Course;
import com.aquabasilea.course.user.Course.CourseBuilder;
import com.aquabasilea.rest.model.CourseLocationDto;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.UUID;

import static java.util.Objects.isNull;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record CourseDto(String id, String courseName, String dayOfWeek, String timeOfTheDay,
                        CourseLocationDto courseLocationDto, boolean isPaused, boolean isCurrentCourse) {

   public static Course map2Course(CourseDto courseDto) {
      String currentId = isNull(courseDto.id) ? UUID.randomUUID().toString() : courseDto.id;
      return CourseBuilder.builder()
              .withId(currentId)
              .withCourseName(courseDto.courseName())
              .withDayOfWeek(courseDto.dayOfWeek())
              .withTimeOfTheDay(courseDto.timeOfTheDay())
              .withIsPaused(courseDto.isPaused())
              .withCourseLocation(CourseLocation.valueOf(courseDto.courseLocationDto.courseLocationKey()))
              .build();
   }

   public static CourseDto of(Course course, boolean isCurrentCourse) {
      return new CourseDto(course.getId(), course.getCourseName(), course.getDayOfWeek(),
              course.getTimeOfTheDay(), CourseLocationDto.of(course.getCourseLocation()), course.getIsPaused(), isCurrentCourse);
   }
}


