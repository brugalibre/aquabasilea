package com.aquabasilea.domain.courselocation.model.repository;

import com.aquabasilea.domain.courselocation.model.CourseLocation;
import com.brugalibre.common.domain.repository.CommonDomainRepository;

public interface CourseLocationRepository extends CommonDomainRepository<CourseLocation> {
   /**
    * Finds a {@link CourseLocation} by its center-id
    *
    * @param centerId the (external) id of the {@link CourseLocation}
    * @return a {@link CourseLocation} instance for the given center id
    */
   CourseLocation findByCenterId(String centerId);
}
