package com.aquabasilea.model.course.coursedef.repository.impl;

import com.aquabasilea.model.course.coursedef.CourseDef;
import com.aquabasilea.model.course.coursedef.repository.CourseDefRepository;
import com.aquabasilea.model.course.coursedef.repository.mapping.CoursesDefEntityMapperImpl;
import com.aquabasilea.persistence.entity.course.aquabasilea.CourseDefEntity;
import com.aquabasilea.persistence.entity.course.aquabasilea.dao.CoursesDefDao;
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
