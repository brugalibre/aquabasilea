package com.aquabasilea.rest.model.course;

import com.aquabasilea.course.Course;
import com.aquabasilea.course.CourseComparator;
import com.aquabasilea.course.WeeklyCourses;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class WeeklyCoursesDto {
   private final List<CourseDto> courseDtos;

   public WeeklyCoursesDto(List<CourseDto> courseDtos) {
      this.courseDtos = courseDtos;
   }

   public static WeeklyCoursesDto of(WeeklyCourses weeklyCourses, Course currentCourse) {
      return new WeeklyCoursesDto(weeklyCourses.getCourses()
              .stream()
              .sorted(new CourseComparator())
              .map(course -> CourseDto.of(course, isCurrentCourse(weeklyCourses, currentCourse, course)))
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

   public List<CourseDto> getCourseDtos() {
      return courseDtos;
   }
}
