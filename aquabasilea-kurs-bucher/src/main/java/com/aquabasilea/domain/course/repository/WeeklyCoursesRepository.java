package com.aquabasilea.domain.course.repository;

import com.aquabasilea.domain.course.model.WeeklyCourses;
import com.brugalibre.domain.user.repository.UserRelatedRepository;

/**
 * The {@link WeeklyCoursesRepository} is responsible for loading and saving a {@link WeeklyCourses}
 */
public interface WeeklyCoursesRepository extends UserRelatedRepository<WeeklyCourses> {
   // no-op
}
