package com.aquabasilea.rest.model.course.user;


import com.aquabasilea.course.CourseLocation;
import com.aquabasilea.course.user.Course;
import com.aquabasilea.course.user.Course.CourseBuilder;
import com.aquabasilea.rest.model.CourseLocationDto;
import com.aquabasilea.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.UUID;

import static java.util.Objects.isNull;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record CourseDto(String id, String courseName, String dayOfWeek, String timeOfTheDay, String courseDateAsString,
                        CourseLocationDto courseLocationDto, boolean isPaused, boolean hasCourseDef, boolean isCurrentCourse) {

   public static Course map2Course(CourseDto courseDto, Locale locale) {
      String currentId = isNull(courseDto.id) ? UUID.randomUUID().toString() : courseDto.id;
      DayOfWeek dayOfWeekEnum = DateUtil.getDayOfWeekFromInput(courseDto.dayOfWeek(), locale);
      return CourseBuilder.builder()
              .withId(currentId)
              .withCourseName(courseDto.courseName())
              .withDayOfWeek(dayOfWeekEnum)
              .withTimeOfTheDay(courseDto.timeOfTheDay())
              .withIsPaused(courseDto.isPaused())
              .withHasCourseDef(courseDto.hasCourseDef)
              .withCourseLocation(CourseLocation.valueOf(courseDto.courseLocationDto.courseLocationKey()))
              .build();
   }

   public static CourseDto of(Course course, boolean isCurrentCourse, Locale locale) {
      return new CourseDto(course.getId(), course.getCourseName(), course.getDayOfWeek().getDisplayName(TextStyle.FULL, locale),
              course.getTimeOfTheDay(), DateUtil.toString(course.getCourseDate(), locale), CourseLocationDto.of(course.getCourseLocation()),
              course.getIsPaused(), course.getHasCourseDef(), isCurrentCourse);
   }
}


