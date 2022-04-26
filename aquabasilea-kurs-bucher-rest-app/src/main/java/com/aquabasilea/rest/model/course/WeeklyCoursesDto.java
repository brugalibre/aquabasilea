package com.aquabasilea.rest.model.course;

import com.aquabasilea.course.Course;
import com.aquabasilea.course.CourseComparator;
import com.aquabasilea.course.WeeklyCourses;

import java.util.List;
import java.util.stream.Collectors;

public class WeeklyCoursesDto {
   private final List<CourseDto> courseDtos;

   public WeeklyCoursesDto(List<CourseDto> courseDtos) {
      this.courseDtos = courseDtos;
   }

   public static WeeklyCoursesDto of(WeeklyCourses weeklyCourses, Course currentCourse) {
      return new WeeklyCoursesDto(weeklyCourses.getCourses()
              .stream()
              .sorted(new CourseComparator())
              .map(course -> CourseDto.of(course, course.getId().equals(currentCourse.getId())))
              .collect(Collectors.toList()));
   }

   public List<CourseDto> getCourseDtos() {
      return courseDtos;
   }
}
