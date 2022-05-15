package com.aquabasilea.course.aquabasilea.repository;

import com.aquabasilea.course.aquabasilea.CourseDef;

import java.util.List;

/**
 * The {@link CourseDefRepository} is responsible for loading and saving {@link CourseDef}es
 */
public interface CourseDefRepository {
   /**
    * Finds all persistent {@link CourseDef}s
    *
    * @return all persistent {@link CourseDef}s
    */
   List<CourseDef> findAllCourseDefs();

   /**
    * Saves the given {@link CourseDef}s
    *
    * @param courseDefs the given {@link CourseDef}s to persist
    */
   void saveAll(List<CourseDef> courseDefs);

   /**
    * Deletes all {@link CourseDef}s
    */
   void deleteAll();
}
