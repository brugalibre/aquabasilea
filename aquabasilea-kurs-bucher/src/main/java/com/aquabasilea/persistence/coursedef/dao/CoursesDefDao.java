package com.aquabasilea.persistence.coursedef.dao;

import com.aquabasilea.persistence.coursedef.CourseDefEntity;
import com.brugalibre.common.domain.repository.NoDomainModelFoundException;
import com.brugalibre.domain.user.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CoursesDefDao extends CrudRepository<CourseDefEntity, String> {
   /**
    * Returns an entity which belongs to the given user id
    *
    * @param userId the id of the {@link User}
    * @return an entity from type {@link CourseDefEntity} which belongs to the given user id
    * @throws NoDomainModelFoundException if there is no {@link CourseDefEntity} associated with the given user-id
    */
   CourseDefEntity getByUserId(String userId);

   /**
    * Returns a list from type {@link CourseDefEntity} which all belong to the given user id
    *
    * @param userId the id of the {@link User}
    * @return a list with all CourseDefEntities which belongs to the given user id
    */
   List<CourseDefEntity> getAllByUserId(String userId);
}
