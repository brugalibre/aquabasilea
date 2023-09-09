package com.aquabasilea.rest.model.admin;

import com.aquabasilea.domain.admin.model.Course4AdminView;
import com.aquabasilea.domain.course.model.Course;
import com.aquabasilea.rest.model.course.weeklycourses.CourseDto;
import com.aquabasilea.rest.model.coursebooker.CourseLocationDto;
import com.aquabasilea.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * A {@link CourseDto} for the admin view
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record Course4AdminViewDto(String id, String courseName, String dayOfWeek, String timeOfTheDay,
                                  LocalDateTime courseDate, CourseLocationDto courseLocationDto, boolean isPaused,
                                  boolean isAppPaused, boolean hasCourseDef, String username, String userId) {
   /**
    * Returns a new {@link CourseDto} for the given {@link Course}, which will be marked as 'current' course4AdminView
    * As {@link Locale} we'll use the current set {@link Locale}
    *
    * @param course4AdminView the course4AdminView for which a {@link CourseDto} is build
    * @param locale           the {@link Locale} for which the ui display texts are resolved
    * @return a new {@link CourseDto}
    */
   public static Course4AdminViewDto of(Course4AdminView course4AdminView, Locale locale) {
      return new Course4AdminViewDto(course4AdminView.id(), course4AdminView.courseName(), course4AdminView.courseDate().getDayOfWeek().getDisplayName(TextStyle.FULL, locale),
              DateUtil.getTimeAsString(course4AdminView.courseDate()), course4AdminView.courseDate(), CourseLocationDto.of(course4AdminView.courseLocation()),
              course4AdminView.isPaused(), course4AdminView.isAppPaused(), course4AdminView.hasCourseDef(), course4AdminView.username(), course4AdminView.userId());
   }
}
