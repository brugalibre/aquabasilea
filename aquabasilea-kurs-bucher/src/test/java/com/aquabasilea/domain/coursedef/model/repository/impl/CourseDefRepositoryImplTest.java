package com.aquabasilea.domain.coursedef.model.repository.impl;

import com.aquabasilea.domain.coursedef.model.CourseDef;
import com.aquabasilea.domain.coursedef.model.repository.CourseDefRepository;
import com.aquabasilea.domain.courselocation.model.CourseLocation;
import com.aquabasilea.domain.courselocation.model.repository.CourseLocationRepository;
import com.aquabasilea.persistence.config.TestAquabasileaCourseBookerPersistenceConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static com.aquabasilea.test.TestConstants.MIGROS_FITNESSCENTER_AQUABASILEA;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = TestAquabasileaCourseBookerPersistenceConfig.class)
class CourseDefRepositoryImplTest {

   @Autowired
   private CourseDefRepository courseDefRepository;

   @Autowired
   private CourseLocationRepository courseLocationRepository;

   @AfterEach
   public void cleanUp() {
      courseDefRepository.deleteAll();
      courseLocationRepository.deleteAll();
   }

   @Test
   void createCourseDef() {
      // Given
      courseLocationRepository.save(MIGROS_FITNESSCENTER_AQUABASILEA);
      CourseLocation courseLocation = courseLocationRepository.findByCenterId(MIGROS_FITNESSCENTER_AQUABASILEA.centerId());
      String intstructor = "Katja";
      String userId = "user1";
      CourseDef courseDef = new CourseDef(null, userId, LocalDateTime.now(), courseLocation, "BeBo-Fitness", intstructor);
      courseDefRepository.save(courseDef);

      // When
      List<CourseDef> courseDefs = courseDefRepository.getAllByUserId(userId);

      // Then
      assertThat(courseDefs.size()).isEqualTo(1);
      CourseDef courseDef1 = courseDefs.get(0);
      assertThat(courseDef1.courseLocation()).isEqualTo(courseLocation);
      assertThat(courseDef1.courseInstructor()).isEqualTo(intstructor);
   }
}