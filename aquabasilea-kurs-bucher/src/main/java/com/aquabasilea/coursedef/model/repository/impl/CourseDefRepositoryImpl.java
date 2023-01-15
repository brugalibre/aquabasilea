package com.aquabasilea.coursedef.model.repository.impl;

import com.aquabasilea.coursedef.model.CourseDef;
import com.aquabasilea.coursedef.model.repository.CourseDefRepository;
import com.aquabasilea.coursedef.model.repository.mapping.CoursesDefEntityMapperImpl;
import com.aquabasilea.coursedef.persistence.CourseDefEntity;
import com.aquabasilea.coursedef.persistence.dao.CoursesDefDao;
import com.brugalibre.common.domain.repository.CommonDomainRepositoryImpl;

import java.util.List;

public class CourseDefRepositoryImpl extends CommonDomainRepositoryImpl<CourseDef, CourseDefEntity, CoursesDefDao>
        implements CourseDefRepository {
   public CourseDefRepositoryImpl(CoursesDefDao coursesDefDao) {
      super(coursesDefDao, new CoursesDefEntityMapperImpl());
   }

   @Override
   public void deleteAllByUserId(String userId) {
      // yes, maybe this could be done faster with a custom query. Maybe tomorrow..
      domainDao.getAllByUserId(userId)
              .forEach(domainDao::delete);
   }

   @Override
   public List<CourseDef> getAllByUserId(String userId) {
      return domainModelMapper.map2DomainModels(domainDao.getAllByUserId(userId));
   }
}
