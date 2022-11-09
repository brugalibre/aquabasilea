package com.aquabasilea.model.course.coursedef.repository;

import com.aquabasilea.model.course.coursedef.CourseDef;
import com.brugalibre.common.domain.repository.CommonDomainRepository;
import com.brugalibre.domain.user.model.User;
import com.brugalibre.domain.user.repository.UserRelatedRepository;

import java.util.List;

/**
 * The {@link CourseDefRepository} is responsible for loading and saving {@link CourseDef}es
 */
public interface CourseDefRepository extends CommonDomainRepository<CourseDef> {
   /**
    * Returns all {@link CourseDef}s which belongs to the given user id
    *
    * @param userId the id of the {@link User}
    * @return a list with all {@link CourseDef}s which belongs to the given user id
    */
   List<CourseDef> getAllByUserId(String userId);

   /**
    * Deletes all {@link CourseDef}s which belongs to the given user id
    *
    * @param userId the id of the {@link User}
    */
   void deleteAllByUserId(String userId);
}
