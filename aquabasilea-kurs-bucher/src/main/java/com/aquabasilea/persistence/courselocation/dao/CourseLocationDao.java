package com.aquabasilea.persistence.courselocation.dao;

import com.aquabasilea.persistence.courselocation.CourseLocationEntity;
import org.springframework.data.repository.CrudRepository;

public interface CourseLocationDao extends CrudRepository<CourseLocationEntity, String> {
   /**
    * Finds a {@link CourseLocationEntity} by its center-id
    *
    * @param centerId the (external) id of the {@link CourseLocationEntity}
    * @return a {@link CourseLocationEntity} instance for the given center id
    */
   CourseLocationEntity findByCenterId(String centerId);
}
