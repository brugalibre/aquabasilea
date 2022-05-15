package com.aquabasilea.course.user.repository.impl;

import com.aquabasilea.course.user.WeeklyCourses;
import com.aquabasilea.course.user.repository.WeeklyCoursesRepository;
import com.aquabasilea.course.user.repository.mapping.WeeklyCoursesEntityMapper;
import com.aquabasilea.course.user.repository.mapping.WeeklyCoursesEntityMapperImpl;
import com.aquabasilea.persistence.entity.course.user.WeeklyCoursesEntity;
import com.aquabasilea.persistence.entity.course.user.dao.WeeklyCoursesDao;

import java.util.Iterator;

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
      WeeklyCoursesEntity weeklyCoursesEntity = findFirstWeeklyCoursesInternal();
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

   private WeeklyCoursesEntity findFirstWeeklyCoursesInternal() {
      Iterable<WeeklyCoursesEntity> weeklyCoursesDaoAll = weeklyCoursesDao.findAll();
      Iterator<WeeklyCoursesEntity> iterator = weeklyCoursesDaoAll.iterator();
      if (iterator.hasNext()) {
         return iterator.next();
      }
      return null;
   }
}
