package com.aquabasilea.coursebooker.model.course.weeklycourses.repository.impl;

import com.aquabasilea.coursebooker.model.course.weeklycourses.Course.CourseBuilder;
import com.aquabasilea.coursebooker.model.course.weeklycourses.WeeklyCourses;
import com.aquabasilea.coursebooker.model.course.weeklycourses.repository.WeeklyCoursesRepository;
import com.aquabasilea.coursebooker.persistence.course.weeklycourses.WeeklyCoursesEntity;
import com.aquabasilea.coursebooker.persistence.course.weeklycourses.dao.WeeklyCoursesDao;
import com.brugalibre.common.domain.repository.NoDomainModelFoundException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class WeeklyCoursesRepositoryImplTest {

   private static final String USER_ID = "123";

   @Test
   void saveWeeklyCourses() {
      // Given
      WeeklyCoursesDao weeklyCoursesDao = new TestWeeklyCoursesDao();
      WeeklyCoursesRepository weeklyCoursesRepository = new WeeklyCoursesRepositoryImpl(weeklyCoursesDao);
      WeeklyCourses weeklyCourses = new WeeklyCourses(USER_ID, List.of(CourseBuilder.builder()
              .withCourseName("Test")
              .withCourseDate(LocalDateTime.now())
              .withIsPaused(true)
              .withHasCourseDef(true)
              .build()));

      // When
      weeklyCoursesRepository.save(weeklyCourses);
      weeklyCourses = weeklyCoursesRepository.getByUserId(USER_ID);

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
      public Optional<WeeklyCoursesEntity> findById(String uuid) {
         return Optional.empty();
      }

      @Override
      public boolean existsById(String uuid) {
         return false;
      }

      @Override
      public Iterable<WeeklyCoursesEntity> findAll() {
         return weeklyCoursesEntities;
      }

      @Override
      public Iterable<WeeklyCoursesEntity> findAllById(Iterable<String> uuids) {
         return null;
      }

      @Override
      public long count() {
         return 1;
      }

      @Override
      public void deleteById(String uuid) {

      }

      @Override
      public void delete(WeeklyCoursesEntity entity) {
         this.weeklyCoursesEntities.remove(entity);
      }

      @Override
      public void deleteAllById(Iterable<? extends String> uuids) {

      }

      @Override
      public void deleteAll(Iterable<? extends WeeklyCoursesEntity> entities) {

      }

      @Override
      public void deleteAll() {
         this.weeklyCoursesEntities.clear();
      }

      @Override
      public WeeklyCoursesEntity getByUserId(String userId) {
         if (!weeklyCoursesEntities.isEmpty() && weeklyCoursesEntities.get(0).getUserId().equals(userId)) {
            return weeklyCoursesEntities.get(0);
         }
         throw new NoDomainModelFoundException("No WeeklyCourses found for user id '" + userId + "'");
      }
   }
}