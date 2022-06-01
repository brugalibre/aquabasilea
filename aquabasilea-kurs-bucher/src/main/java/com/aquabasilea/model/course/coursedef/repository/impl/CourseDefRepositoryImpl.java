package com.aquabasilea.model.course.coursedef.repository.impl;

import com.aquabasilea.model.course.coursedef.CourseDef;
import com.aquabasilea.model.course.coursedef.repository.CourseDefRepository;
import com.aquabasilea.model.course.coursedef.repository.mapping.CoursesDefEntityMapper;
import com.aquabasilea.model.course.coursedef.repository.mapping.CoursesDefEntityMapperImpl;
import com.aquabasilea.persistence.entity.course.aquabasilea.CourseDefEntity;
import com.aquabasilea.persistence.entity.course.aquabasilea.dao.CoursesDefDao;

import java.util.ArrayList;
import java.util.List;

public class CourseDefRepositoryImpl implements CourseDefRepository {
   private final CoursesDefDao coursesDefDao;
   private final CoursesDefEntityMapper coursesDefEntityMapper;

   public CourseDefRepositoryImpl(CoursesDefDao coursesDefDao) {
      this.coursesDefDao = coursesDefDao;
      this.coursesDefEntityMapper = new CoursesDefEntityMapperImpl();
   }

   @Override
   public List<CourseDef> findAllCourseDefs() {
      List<CourseDefEntity> courseDefEntities = new ArrayList<>();
      coursesDefDao.findAll().forEach(courseDefEntities::add);
      return new ArrayList<>(coursesDefEntityMapper.map2CourseDefs(courseDefEntities));
   }

   @Override
   public void saveAll(List<CourseDef> courseDefs) {
      coursesDefDao.saveAll(coursesDefEntityMapper.map2CourseDefEntities(courseDefs));
   }

   @Override
   public void deleteAll() {
      coursesDefDao.deleteAll();
   }
}
