package com.aquabasilea.model.course.weeklycourses.repository.impl;

import com.aquabasilea.model.course.weeklycourses.WeeklyCourses;
import com.aquabasilea.model.course.weeklycourses.repository.WeeklyCoursesRepository;
import com.aquabasilea.model.course.weeklycourses.repository.mapping.WeeklyCoursesEntityMapperImpl;
import com.aquabasilea.persistence.entity.course.weeklycourses.WeeklyCoursesEntity;
import com.aquabasilea.persistence.entity.course.weeklycourses.dao.WeeklyCoursesDao;
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
