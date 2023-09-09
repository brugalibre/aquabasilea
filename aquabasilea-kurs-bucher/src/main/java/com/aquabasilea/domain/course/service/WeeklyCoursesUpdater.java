package com.aquabasilea.domain.course.service;

import com.aquabasilea.domain.course.model.Course;
import com.aquabasilea.domain.course.model.WeeklyCourses;
import com.aquabasilea.domain.course.repository.WeeklyCoursesRepository;
import com.aquabasilea.domain.coursedef.model.repository.CourseDefRepository;

/**
 * The {@link WeeklyCoursesUpdater} is responsible for updating the {@link Course#getHasCourseDef()} flag
 */
public class WeeklyCoursesUpdater {

private final WeeklyCoursesRepository weeklyCoursesRepository;
private final CourseDefRepository courseDefRepository;

    public WeeklyCoursesUpdater(WeeklyCoursesRepository weeklyCoursesRepository, CourseDefRepository courseDefRepository) {
        this.weeklyCoursesRepository = weeklyCoursesRepository;
        this.courseDefRepository = courseDefRepository;
    }

    /**
     * During the initializing state the course-dates of the {@link Course}s may be updated, when this date lays in the past
     * Additionally the {@link Course#getHasCourseDef()} must be updated, when this course-date has changed
     *
     * @param weeklyCourses the WeeklyCourses to update
     */
    public void updateCoursesHasCourseDef(WeeklyCourses weeklyCourses) {
        weeklyCourses.updateCoursesHasCourseDef(courseDefRepository.getAllByUserId(weeklyCourses.getUserId()));
        weeklyCoursesRepository.save(weeklyCourses);
    }
}
