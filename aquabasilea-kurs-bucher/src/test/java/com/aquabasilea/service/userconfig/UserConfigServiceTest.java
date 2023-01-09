package com.aquabasilea.service.userconfig;

import com.aquabasilea.model.course.CourseLocation;
import com.aquabasilea.model.userconfig.UserConfig;
import com.aquabasilea.model.userconfig.repository.UserConfigRepository;
import com.aquabasilea.persistence.config.TestAquabasileaCourseBookerPersistenceConfig;
import com.brugalibre.domain.contactpoint.mobilephone.model.MobilePhone;
import com.brugalibre.domain.user.model.User;
import com.brugalibre.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(classes = TestAquabasileaCourseBookerPersistenceConfig.class)
class UserConfigServiceTest {

   @Autowired
   private UserConfigRepository userConfigRepository;

   @Autowired
   private UserRepository userRepository;

   @Test
   void updateCourseLocations() {
      // Given
      String userId = userRepository.save(User.of("peter", "password", MobilePhone.of("0791234567"))).id();
      userConfigRepository.save(new UserConfig(userId, List.of(CourseLocation.FITNESSPARK_HEUWAAGE)));
      UserConfigService userConfigService = new UserConfigService(userConfigRepository);
      List<CourseLocation> courseLocations = List.of(CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA, CourseLocation.FITNESSPARK_HEUWAAGE);

      // When
      UserConfig userConfig = userConfigService.updateCourseLocations(userId, courseLocations);

      // Then
      assertThat(userConfig.getCourseLocations(), is(courseLocations));
   }
}