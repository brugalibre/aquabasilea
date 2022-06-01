package com.aquabasilea.model.course.weeklycourses.repository.impl;

import com.aquabasilea.model.course.weeklycourses.WeeklyCourses;
import com.aquabasilea.model.course.weeklycourses.repository.WeeklyCoursesRepository;
import com.aquabasilea.model.course.weeklycourses.repository.mapping.WeeklyCoursesEntityMapper;
import com.aquabasilea.model.course.weeklycourses.repository.mapping.WeeklyCoursesEntityMapperImpl;
import com.aquabasilea.persistence.entity.course.weeklycourses.WeeklyCoursesEntity;
import com.aquabasilea.persistence.entity.course.weeklycourses.dao.WeeklyCoursesDao;
import com.aquabasilea.persistence.repository.SingleEntityRepositoryUtil;

import static java.util.Objects.isNull;

public class WeeklyCoursesRepositoryImpl implements WeeklyCoursesRepository {
   private final WeeklyCoursesDao weeklyCoursesDao;
   private final WeeklyCoursesEntityMapper weeklyCoursesEntityMapper;

   public WeeklyCoursesRepositoryImpl(WeeklyCoursesDao weeklyCoursesDao) {
      this.weeklyCoursesDao = weeklyCoursesDao;
      this.weeklyCoursesEntityMapper = new WeeklyCoursesEntityMapperImpl();
   }

   @Override
   public WeeklyCourses findFirstWeeklyCourses() {
      WeeklyCoursesEntity weeklyCoursesEntity = SingleEntityRepositoryUtil.findFirstEntity(weeklyCoursesDao);
      if (isNull(weeklyCoursesEntity)) {
         WeeklyCourses weeklyCourses = new WeeklyCourses();
         return save(weeklyCourses);
      }
      return weeklyCoursesEntityMapper.map2WeeklyCourses(weeklyCoursesEntity);
   }

   @Override
   public WeeklyCourses save(WeeklyCourses weeklyCourses) {
      WeeklyCoursesEntity weeklyCoursesEntity = weeklyCoursesEntityMapper.map2WeeklyCoursesEntity(weeklyCourses);
      WeeklyCoursesEntity savedWeeklyCoursesEntity = weeklyCoursesDao.save(weeklyCoursesEntity);
      return weeklyCoursesEntityMapper.map2WeeklyCourses(savedWeeklyCoursesEntity);
   }

   @Override
   public void deleteAll() {
      weeklyCoursesDao.deleteAll();
   }
}
