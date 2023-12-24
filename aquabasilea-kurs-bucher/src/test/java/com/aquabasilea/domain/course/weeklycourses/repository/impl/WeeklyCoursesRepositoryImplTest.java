package com.aquabasilea.domain.course.weeklycourses.repository.impl;

import com.aquabasilea.domain.course.model.Course.CourseBuilder;
import com.aquabasilea.domain.course.model.WeeklyCourses;
import com.aquabasilea.domain.course.repository.WeeklyCoursesRepository;
import com.aquabasilea.domain.courselocation.model.CourseLocation;
import com.aquabasilea.domain.courselocation.model.repository.CourseLocationRepository;
import com.aquabasilea.persistence.config.TestAquabasileaCourseBookerPersistenceConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static com.aquabasilea.test.TestConstants.FITNESSPARK_GLATTPARK;
import static com.aquabasilea.test.TestConstants.MIGROS_FITNESSCENTER_AQUABASILEA;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(classes = TestAquabasileaCourseBookerPersistenceConfig.class)
class WeeklyCoursesRepositoryImplTest {

   private static final String USER_ID = "123";

   @Autowired
   private WeeklyCoursesRepository weeklyCoursesRepository;

   @Autowired
   private CourseLocationRepository courseLocationRepository;


   @BeforeEach
   public void setUp() {
      courseLocationRepository.saveAll(List.of(MIGROS_FITNESSCENTER_AQUABASILEA, FITNESSPARK_GLATTPARK));
   }

   @AfterEach
   public void cleanUp() {
      this.weeklyCoursesRepository.deleteAll();
      this.courseLocationRepository.deleteAll();
   }

   @Test
   void saveWeeklyCourses() {
      // Given
      CourseLocation courseLocation = courseLocationRepository.findByCenterId(MIGROS_FITNESSCENTER_AQUABASILEA.centerId());
      WeeklyCourses weeklyCourses = new WeeklyCourses(USER_ID, List.of(CourseBuilder.builder()
              .withCourseName("Test")
              .withCourseDate(LocalDateTime.now())
              .withIsPaused(true)
              .withCourseLocation(courseLocation)
              .withCourseInstructor("Peter")
              .withHasCourseDef(true)
              .build()));

      // When
      weeklyCoursesRepository.save(weeklyCourses);
      weeklyCourses = weeklyCoursesRepository.getByUserId(USER_ID);

      // Then
      assertThat(weeklyCourses.getCourses().get(0).getIsPaused(), is(true));
   }

   @Test
   void saveAndUpdateWeeklyCourses() {
      // Given
      CourseLocation courseLocation1 = courseLocationRepository.findByCenterId(MIGROS_FITNESSCENTER_AQUABASILEA.centerId());
      WeeklyCourses weeklyCourses = new WeeklyCourses(USER_ID, List.of(CourseBuilder.builder()
              .withCourseName("Test")
              .withCourseDate(LocalDateTime.now())
              .withIsPaused(true)
              .withCourseLocation(courseLocation1)
              .withCourseInstructor("Peter")
              .withHasCourseDef(true)
              .build()));
      weeklyCourses = weeklyCoursesRepository.save(weeklyCourses);
      weeklyCourses.addCourse(CourseBuilder.builder()
              .withCourseName("Test2")
              .withCourseDate(LocalDateTime.now())
              .withIsPaused(false)
              .withCourseLocation(courseLocation1)
              .withCourseInstructor("heinz")
              .withHasCourseDef(true)
              .build());

      // When
      weeklyCourses = weeklyCoursesRepository.save(weeklyCourses);

      // Then
      assertThat(weeklyCourses.getCourses().get(0).getIsPaused(), is(true));
   }
}