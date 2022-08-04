package com.aquabasilea.model.course.repository.impl;

import com.aquabasilea.model.course.weeklycourses.Course.CourseBuilder;
import com.aquabasilea.model.course.weeklycourses.WeeklyCourses;
import com.aquabasilea.model.course.weeklycourses.repository.WeeklyCoursesRepository;
import com.aquabasilea.model.course.weeklycourses.repository.impl.WeeklyCoursesRepositoryImpl;
import com.aquabasilea.persistence.entity.course.weeklycourses.WeeklyCoursesEntity;
import com.aquabasilea.persistence.entity.course.weeklycourses.dao.WeeklyCoursesDao;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class WeeklyCoursesRepositoryImplTest {

   @Test
   void saveWeeklyCourses() {
      // Given
      WeeklyCoursesDao weeklyCoursesDao = new TestWeeklyCoursesDao();
      WeeklyCoursesRepository weeklyCoursesRepository = new WeeklyCoursesRepositoryImpl(weeklyCoursesDao);
      WeeklyCourses weeklyCourses = new WeeklyCourses(List.of(CourseBuilder.builder()
              .withCourseName("Test")
              .withCourseDate(LocalDateTime.now())
              .withIsPaused(true)
              .withHasCourseDef(true)
              .build()));

      // When
      weeklyCoursesRepository.save(weeklyCourses);
      weeklyCourses = weeklyCoursesRepository.findFirstWeeklyCourses();

      // Then
      assertThat(weeklyCourses.getCourses().get(0).getIsPaused(), is(true));
   }


   private static class TestWeeklyCoursesDao implements WeeklyCoursesDao {
      private final List<WeeklyCoursesEntity> weeklyCoursesEntities;

      private TestWeeklyCoursesDao() {
         this.weeklyCoursesEntities = new ArrayList<>();
      }

      @Override
      public <S extends WeeklyCoursesEntity> S save(S entity) {
         weeklyCoursesEntities.add(entity);
         return entity;
      }

      @Override
      public <S extends WeeklyCoursesEntity> Iterable<S> saveAll(Iterable<S> entities) {
         return null;
      }

      @Override
      public Optional<WeeklyCoursesEntity> findById(UUID uuid) {
         return Optional.empty();
      }

      @Override
      public boolean existsById(UUID uuid) {
         return false;
      }

      @Override
      public Iterable<WeeklyCoursesEntity> findAll() {
         return weeklyCoursesEntities;
      }

      @Override
      public Iterable<WeeklyCoursesEntity> findAllById(Iterable<UUID> uuids) {
         return null;
      }

      @Override
      public long count() {
         return 1;
      }

      @Override
      public void deleteById(UUID uuid) {

      }

      @Override
      public void delete(WeeklyCoursesEntity entity) {
         this.weeklyCoursesEntities.remove(entity);

      }

      @Override
      public void deleteAllById(Iterable<? extends UUID> uuids) {

      }

      @Override
      public void deleteAll(Iterable<? extends WeeklyCoursesEntity> entities) {

      }

      @Override
      public void deleteAll() {
         this.weeklyCoursesEntities.clear();
      }
   }
}