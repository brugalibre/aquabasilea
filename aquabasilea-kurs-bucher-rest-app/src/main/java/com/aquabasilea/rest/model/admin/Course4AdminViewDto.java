package com.aquabasilea.rest.model.admin;

import com.aquabasilea.coursebooker.AquabasileaCourseBooker;
import com.aquabasilea.coursebooker.model.course.weeklycourses.Course;
import com.aquabasilea.rest.model.CourseLocationDto;
import com.aquabasilea.rest.model.course.weeklycourses.CourseDto;
import com.aquabasilea.util.DateUtil;
import com.brugalibre.domain.user.model.User;
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
    * Returns a new {@link CourseDto} for the given {@link Course}, which will be marked as 'current' course
    * As {@link Locale} we'll use the current set {@link Locale}
    *
    * @param course      the course for which a {@link CourseDto} is build
    * @param user        the user of the user this {@link Course4AdminViewDto} belongs to
    * @param locale      the {@link Locale} for which the ui display texts are resolved
    * @param isAppPaused <code>true</code> if the {@link AquabasileaCourseBooker} is pause or <code>false </code> if not
    * @return a new {@link CourseDto}
    */
   public static Course4AdminViewDto of(Course course, User user, Locale locale, boolean isAppPaused) {
      return new Course4AdminViewDto(course.getId(), course.getCourseName(), course.getCourseDate().getDayOfWeek().getDisplayName(TextStyle.FULL, locale),
              DateUtil.getTimeAsString(course.getCourseDate()), course.getCourseDate(), CourseLocationDto.of(course.getCourseLocation()),
              course.getIsPaused(), isAppPaused, course.getHasCourseDef(), user.username(), user.id());
   }
}
