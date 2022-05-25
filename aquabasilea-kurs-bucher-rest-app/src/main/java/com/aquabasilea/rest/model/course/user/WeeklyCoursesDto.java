package com.aquabasilea.rest.model.course.user;


import com.aquabasilea.course.user.Course;
import com.aquabasilea.course.user.CourseComparator;
import com.aquabasilea.course.user.WeeklyCourses;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;


@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record WeeklyCoursesDto (List<CourseDto> courseDtos){

   public static WeeklyCoursesDto of(WeeklyCourses weeklyCourses, Course currentCourse, Locale locale) {
      return new WeeklyCoursesDto(weeklyCourses.getCourses()
              .stream()
              .sorted(new CourseComparator())
              .map(course -> CourseDto.of(course, isCurrentCourse(weeklyCourses, currentCourse, course), locale))
              .collect(Collectors.toList()));
   }

   private static boolean isCurrentCourse(WeeklyCourses weeklyCourses, Course currentCourse, Course course) {
      // If the app is paused, then there is no current course
      if (isNull(currentCourse)) {
         return false;
      }
      // if we're adding a new course, then there is only one. And this one don't have an id yet
      if (weeklyCourses.getCourses().size() == 1) {
         return true;
      }
      return nonNull(course.getId()) && course.getId().equals(currentCourse.getId());
   }
}
