package com.aquabasilea.service.userconfig;

import com.aquabasilea.domain.courselocation.model.CourseLocation;
import com.aquabasilea.domain.courselocation.model.repository.CourseLocationRepository;
import com.aquabasilea.domain.userconfig.model.UserConfig;
import com.aquabasilea.domain.userconfig.repository.UserConfigRepository;
import com.aquabasilea.persistence.config.TestAquabasileaCourseBookerPersistenceConfig;
import com.brugalibre.domain.contactpoint.mobilephone.model.MobilePhone;
import com.brugalibre.domain.user.model.User;
import com.brugalibre.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.aquabasilea.test.TestConstants.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(classes = TestAquabasileaCourseBookerPersistenceConfig.class)
class UserConfigServiceTest {

   @Autowired
   private UserConfigRepository userConfigRepository;

   @Autowired
   private UserRepository userRepository;

   @Autowired
   private CourseLocationRepository courseLocationRepository;

   @BeforeEach
   public void setUp() {
      cleanUp();
   }

   @AfterEach
   public void cleanUp() {
      userRepository.deleteAll();
      userConfigRepository.deleteAll();
      courseLocationRepository.deleteAll();
   }

   @Test
   void updateCourseLocations() {
      // Given
      courseLocationRepository.saveAll(List.of(FITNESSPARK_HEUWAAGE, MIGROS_FITNESSCENTER_AQUABASILEA, FITNESSPARK_GLATTPARK));
      CourseLocation glattpark = courseLocationRepository.findByCenterId(FITNESSPARK_GLATTPARK.centerId());

      String userId = userRepository.save(User.of("peter", "password", MobilePhone.of("0791234567"))).id();
      userConfigRepository.save(new UserConfig(userId, List.of(glattpark)));
      UserConfigService userConfigService = new UserConfigService(userConfigRepository);
      List<CourseLocation> heuwaageAndAquabasilea = List.of(courseLocationRepository.findByCenterId(MIGROS_FITNESSCENTER_AQUABASILEA.centerId()),
              courseLocationRepository.findByCenterId(FITNESSPARK_HEUWAAGE.centerId()));

      // When
      userConfigService.updateCourseLocations(userId, heuwaageAndAquabasilea);
      List<CourseLocation> actualCourseLocationsFirstUpdate = userConfigService.getCourseLocations4UserId(userId);
      userConfigService.updateCourseLocations(userId, List.of());
      List<CourseLocation> actualCourseLocationsSecondUpdate = userConfigService.getCourseLocations4UserId(userId);

      // Then
      assertThat(actualCourseLocationsFirstUpdate, is(heuwaageAndAquabasilea));
      assertThat(actualCourseLocationsSecondUpdate.size(), is(0));
   }
}