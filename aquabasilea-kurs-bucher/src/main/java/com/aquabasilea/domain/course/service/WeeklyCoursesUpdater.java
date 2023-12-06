package com.aquabasilea.domain.course.service;

import com.aquabasilea.domain.course.model.Course;
import com.aquabasilea.domain.course.model.WeeklyCourses;
import com.aquabasilea.domain.course.repository.WeeklyCoursesRepository;
import com.aquabasilea.domain.coursedef.model.repository.CourseDefRepository;

/**
 * The {@link WeeklyCoursesUpdater} is responsible for updating the {@link Course#getHasCourseDef()} flag
 * and for pausing or resuming the {@link Course}s
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

    /**
     * Resumes all {@link Course}s of the {@link WeeklyCourses} if <b>all</b> those courses are currently paused.
     * If there exists only one {@link Course} which is un-paused, then nothing happens!
     *
     */
    public void resumeAllCoursesIfAllPaused(String userId) {
       WeeklyCourses weeklyCourses = this.weeklyCoursesRepository.getByUserId(userId);
       boolean hasOnlyPaused = weeklyCourses.getCourses()
               .stream()
               .allMatch(Course::getIsPaused);
       if (hasOnlyPaused) {
          weeklyCourses.getCourses()
                  .forEach(course -> course.setIsPaused(false));
          weeklyCoursesRepository.save(weeklyCourses);
       }
    }
}
