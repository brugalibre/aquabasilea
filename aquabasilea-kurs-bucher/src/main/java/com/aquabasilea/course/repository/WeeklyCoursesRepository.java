package com.aquabasilea.course.repository;

import com.aquabasilea.course.WeeklyCourses;

/**
 * The {@link WeeklyCoursesRepository} is responsible for loading and saving a {@link WeeklyCourses}
 */
public interface WeeklyCoursesRepository {
   /**
    * Retries the first {@link WeeklyCourses} which is found or creates a new one, if there is none
    *
    * @return the first {@link WeeklyCourses} which is found
    */
   WeeklyCourses findFirstWeeklyCourses();

   /**
    * Saves the given {@link WeeklyCourses}
    *
    * @param weeklyCourses the {@link WeeklyCourses} to save
    * @return the instance of the saved {@link WeeklyCourses}
    */
   WeeklyCourses save(WeeklyCourses weeklyCourses);
}
