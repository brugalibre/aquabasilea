package com.aquabasilea.domain.admin.model;

import com.aquabasilea.domain.coursebooker.AquabasileaCourseBooker;
import com.aquabasilea.domain.course.CourseLocation;
import com.aquabasilea.domain.course.Course;
import com.brugalibre.domain.user.model.User;

import java.time.LocalDateTime;
import java.util.Locale;

public record Course4AdminView(String id, String courseName, LocalDateTime courseDate,
                               CourseLocation courseLocation, boolean isPaused,
                               boolean isAppPaused, boolean hasCourseDef, String username, String userId) {
    /**
     * Returns a new {@link Course4AdminView} for the given {@link Course}, which will be marked as 'current' course
     * As {@link Locale} we'll use the current set {@link Locale}
     *
     * @param course      the course for which a {@link Course} is build
     * @param user        the user to which this {@link Course4AdminView} belongs to
     * @param isAppPaused <code>true</code> if the {@link AquabasileaCourseBooker} is pause or <code>false </code> if not
     * @return a new {@link Course}
     */
    public static Course4AdminView of(Course course, User user, boolean isAppPaused) {
        return new Course4AdminView(course.getId(), course.getCourseName(), course.getCourseDate(), course.getCourseLocation(),
                course.getIsPaused(), isAppPaused, course.getHasCourseDef(), user.username(), user.id());
    }
}
