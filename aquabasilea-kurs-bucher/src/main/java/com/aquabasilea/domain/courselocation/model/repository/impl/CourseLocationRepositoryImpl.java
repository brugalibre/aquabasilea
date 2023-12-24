package com.aquabasilea.domain.courselocation.model.repository.impl;

import com.aquabasilea.domain.courselocation.mapper.CoursesLocationEntityMapperImpl;
import com.aquabasilea.domain.courselocation.model.CourseLocation;
import com.aquabasilea.domain.courselocation.model.repository.CourseLocationRepository;
import com.aquabasilea.persistence.courselocation.CourseLocationEntity;
import com.aquabasilea.persistence.courselocation.dao.CourseLocationDao;
import com.brugalibre.common.domain.repository.CommonDomainRepositoryImpl;

public class CourseLocationRepositoryImpl extends CommonDomainRepositoryImpl<CourseLocation, CourseLocationEntity, CourseLocationDao>
        implements CourseLocationRepository {
   public CourseLocationRepositoryImpl(CourseLocationDao courseLocationDao) {
      super(courseLocationDao, new CoursesLocationEntityMapperImpl());
   }

   @Override
   public CourseLocation findByCenterId(String centerId) {
      return domainModelMapper.map2DomainModel(domainDao.findByCenterId(centerId));
   }
}
