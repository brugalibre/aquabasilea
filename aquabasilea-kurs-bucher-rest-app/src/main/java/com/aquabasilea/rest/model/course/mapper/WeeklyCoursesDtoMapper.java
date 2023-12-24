package com.aquabasilea.rest.model.course.mapper;

import com.aquabasilea.domain.course.model.Course;
import com.aquabasilea.domain.course.model.CourseComparator;
import com.aquabasilea.domain.course.model.WeeklyCourses;
import com.aquabasilea.rest.model.course.weeklycourses.WeeklyCoursesDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
public class WeeklyCoursesDtoMapper {
   private final CourseDtoMapper courseDtoMapper;

   @Autowired
   public WeeklyCoursesDtoMapper(CourseDtoMapper courseDtoMapper) {
      this.courseDtoMapper = courseDtoMapper;
   }

   /**
    * Maps the given {@link WeeklyCourses} to a {@link WeeklyCoursesDto}.
    *
    * @param weeklyCourses the {@link WeeklyCourses} to map
    * @param currentCourse the {@link Course} which is the next {@link Course} to book
    * @return a {@link WeeklyCoursesDto}
    */
   public WeeklyCoursesDto mapToWeeklyCourseDto(WeeklyCourses weeklyCourses, Course currentCourse) {
      return new WeeklyCoursesDto(weeklyCourses.getCourses()
              .stream()
              .sorted(new CourseComparator())
              .map(course -> courseDtoMapper.mapToCourseDto(course, isCurrentCourse(weeklyCourses, currentCourse, course)))
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
