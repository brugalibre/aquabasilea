package com.aquabasilea.domain.course.repository.impl;

import com.aquabasilea.domain.course.model.WeeklyCourses;
import com.aquabasilea.domain.course.repository.WeeklyCoursesRepository;
import com.aquabasilea.domain.course.repository.mapping.WeeklyCoursesEntityMapperImpl;
import com.aquabasilea.persistence.courses.WeeklyCoursesEntity;
import com.aquabasilea.persistence.courses.dao.WeeklyCoursesDao;
import com.brugalibre.common.domain.repository.CommonDomainRepositoryImpl;

public class WeeklyCoursesRepositoryImpl extends CommonDomainRepositoryImpl<WeeklyCourses, WeeklyCoursesEntity, WeeklyCoursesDao>
        implements WeeklyCoursesRepository {
   public WeeklyCoursesRepositoryImpl(WeeklyCoursesDao weeklyCoursesDao) {
      super(weeklyCoursesDao, new WeeklyCoursesEntityMapperImpl());
   }

   @Override
   public WeeklyCourses getByUserId(String userId) {
      return domainModelMapper.map2DomainModel(domainDao.getByUserId(userId));
   }
}
