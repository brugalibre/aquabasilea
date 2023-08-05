package com.aquabasilea.persistence.courses.dao;

import com.aquabasilea.persistence.courses.WeeklyCoursesEntity;
import com.brugalibre.common.domain.repository.NoDomainModelFoundException;
import com.brugalibre.domain.user.model.User;
import org.springframework.data.repository.CrudRepository;

public interface WeeklyCoursesDao extends CrudRepository<WeeklyCoursesEntity, String> {
   /**
    * Returns an entity which belongs to the given user id
    *
    * @param userId the id of the {@link User}
    * @return an entity from type {@link WeeklyCoursesEntity} which belongs to the given user id
    * @throws NoDomainModelFoundException if there is no {@link WeeklyCoursesEntity} associated with the given user-id
    */
   WeeklyCoursesEntity getByUserId(String userId);
}
