package com.aquabasilea.application.initialize.persistence.courselocation;

import com.aquabasilea.domain.coursebooker.states.booking.facade.CourseBookerFacadeFactory;
import com.aquabasilea.domain.coursebooker.states.booking.facade.CourseLocationExtractorFacade;
import com.aquabasilea.domain.courselocation.model.CourseLocation;
import com.aquabasilea.domain.courselocation.model.repository.CourseLocationRepository;
import com.aquabasilea.persistence.config.TestAquabasileaCourseBookerPersistenceConfig;
import com.aquabasilea.test.TestConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static com.aquabasilea.test.TestConstants.FITNESSPARK_HEUWAAGE;
import static com.aquabasilea.test.TestConstants.MIGROS_FITNESSCENTER_AQUABASILEA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@SpringBootTest(classes = TestAquabasileaCourseBookerPersistenceConfig.class)
class CourseLocationInitializerTest {

   @Autowired
   private CourseLocationRepository courseLocationRepository;

   @AfterEach
   public void cleanUp() {
      courseLocationRepository.deleteAll();
   }

   @Test
   void initializeOnAppStart() {
      // Given
      CourseLocationExtractorFacade courseLocationExtractorFacade = mockCourseLocationExtractorFacade();
      CourseBookerFacadeFactory courseBookerFacadeFactory = mockCourseBookerFacadeFactory(courseLocationExtractorFacade);
      CourseLocationInitializer courseLocationInitializer = new CourseLocationInitializer(courseLocationRepository, courseBookerFacadeFactory);

      // When
      courseLocationInitializer.initializeOnAppStart();

      // Then
      List<CourseLocation> courseLocations = courseLocationRepository.getAll();
      assertThat(courseLocations.size()).isEqualTo(2);
      Optional<CourseLocation> aquabasileaLocation = getCourseLocationById(courseLocations, MIGROS_FITNESSCENTER_AQUABASILEA.centerId());
      assertThat(aquabasileaLocation.isPresent()).isTrue();
      Optional<CourseLocation> fitnessparkHeuwaageLocation = getCourseLocationById(courseLocations, FITNESSPARK_HEUWAAGE.centerId());
      assertThat(fitnessparkHeuwaageLocation.isPresent()).isTrue();
   }

   @Test
   void initializeOnAppStart_alreadyInitialized() {
      // Given
      courseLocationRepository.save(TestConstants.FITNESSPARK_GLATTPARK);
      CourseLocationExtractorFacade courseLocationExtractorFacade = mockCourseLocationExtractorFacade();
      CourseBookerFacadeFactory courseBookerFacadeFactory = mockCourseBookerFacadeFactory(courseLocationExtractorFacade);
      CourseLocationInitializer courseLocationInitializer = new CourseLocationInitializer(courseLocationRepository, courseBookerFacadeFactory);

      // When
      courseLocationInitializer.initializeOnAppStart();

      // Then
      verify(courseLocationExtractorFacade, never()).getCourseLocations();
   }

   private static CourseBookerFacadeFactory mockCourseBookerFacadeFactory(CourseLocationExtractorFacade courseLocationExtractorFacade) {
      CourseBookerFacadeFactory courseBookerFacadeFactory = mock(CourseBookerFacadeFactory.class);
      when(courseBookerFacadeFactory.createCourseLocationExtractorFacade()).thenReturn(courseLocationExtractorFacade);
      return courseBookerFacadeFactory;
   }

   private static CourseLocationExtractorFacade mockCourseLocationExtractorFacade() {
      CourseLocationExtractorFacade courseLocationExtractorFacade = spy(CourseLocationExtractorFacade.class);
      when(courseLocationExtractorFacade.getCourseLocations()).thenReturn(List.of(FITNESSPARK_HEUWAAGE, MIGROS_FITNESSCENTER_AQUABASILEA));
      return courseLocationExtractorFacade;
   }

   private static Optional<CourseLocation> getCourseLocationById(List<CourseLocation> courseLocations, String centerId) {
      return courseLocations.stream()
              .filter(courseLocation -> centerId.equals(courseLocation.centerId()))
              .findFirst();
   }
}