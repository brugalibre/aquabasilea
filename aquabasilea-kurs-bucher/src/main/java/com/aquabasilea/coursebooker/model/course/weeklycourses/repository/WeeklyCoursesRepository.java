package com.aquabasilea.coursebooker.model.course.weeklycourses.repository;

import com.aquabasilea.coursebooker.model.course.weeklycourses.WeeklyCourses;
import com.brugalibre.domain.user.repository.UserRelatedRepository;

/**
 * The {@link WeeklyCoursesRepository} is responsible for loading and saving a {@link WeeklyCourses}
 */
public interface WeeklyCoursesRepository extends UserRelatedRepository<WeeklyCourses> {
   // no-op
}
