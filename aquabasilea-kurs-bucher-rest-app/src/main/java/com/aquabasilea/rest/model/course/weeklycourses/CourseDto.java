package com.aquabasilea.rest.model.course.weeklycourses;


import com.aquabasilea.application.i18n.TextResources;
import com.aquabasilea.domain.course.Course;
import com.aquabasilea.domain.course.CourseLocation;
import com.aquabasilea.rest.model.coursebooker.CourseLocationDto;
import com.aquabasilea.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.UUID;

import static java.util.Objects.isNull;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record CourseDto(String id, String courseName, String courseInstructor, String dayOfWeek, String timeOfTheDay,
                        LocalDateTime courseDate, CourseLocationDto courseLocationDto, boolean isPaused,
                        boolean hasCourseDef, boolean isCurrentCourse, String tooltipText) {

   public static Course map2Course(CourseDto courseDto) {
      String currentId = isNull(courseDto.id) ? UUID.randomUUID().toString() : courseDto.id;
      return Course.CourseBuilder.builder()
              .withId(currentId)
              .withCourseName(courseDto.courseName())
              .withCourseInstructor(courseDto.courseInstructor())
              .withCourseDate(courseDto.courseDate)
              .withIsPaused(courseDto.isPaused())
              .withHasCourseDef(courseDto.hasCourseDef)
              .withCourseLocation(CourseLocation.valueOf(courseDto.courseLocationDto.courseLocationKey()))
              .build();
   }

   /**
    * Returns a new {@link CourseDto} for the given {@link Course}, {@link Locale} and boolean which defines if the given course
    * is the current course or not
    *
    * @param course          the course for which a {@link CourseDto} is build
    * @param isCurrentCourse <code>true</code> if the given {@link Course} is the current course or <code>false</code> if not
    * @param locale          the {@link Locale} for which the ui display texts are resolved
    * @return a new {@link CourseDto}
    */
   public static CourseDto of(Course course, boolean isCurrentCourse, Locale locale) {
      return new CourseDto(course.getId(), course.getCourseName(), course.getCourseInstructor(), course.getCourseDate().getDayOfWeek().getDisplayName(TextStyle.FULL, locale),
              DateUtil.getTimeAsString(course.getCourseDate()), course.getCourseDate(), CourseLocationDto.of(course.getCourseLocation()),
              course.getIsPaused(), course.getHasCourseDef(), isCurrentCourse, getTooltipText(course, isCurrentCourse));
   }

   private static String getTooltipText(Course course, boolean isCurrentCourse) {
      if (!course.getHasCourseDef()) {
         return TextResources.TOOLTIP_COURSE_HAS_NO_COURSE_DEF;
      } else if (course.getIsPaused()) {
         return TextResources.TOOLTIP_COURSE_IS_PAUSED;
      } else if (isCurrentCourse) {
         return TextResources.TOOLTIP_COURSE_IS_CURRENT_COURSE.formatted(course.getCourseInstructor());
      }
      return "";
   }
}


