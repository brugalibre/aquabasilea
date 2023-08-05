package com.aquabasilea.application.initialize.coursebooker;

import com.aquabasilea.application.i18n.TextResources;
import com.aquabasilea.application.initialize.api.UserAddedEvent;
import com.aquabasilea.application.initialize.persistence.PersistenceInitializer;
import com.aquabasilea.domain.course.repository.WeeklyCoursesRepository;
import com.aquabasilea.domain.coursebooker.AquabasileaCourseBooker;
import com.aquabasilea.domain.coursebooker.AquabasileaCourseBookerHolder;
import com.aquabasilea.domain.statistics.model.repository.StatisticsRepository;
import com.aquabasilea.domain.userconfig.repository.UserConfigRepository;
import com.aquabasilea.persistence.config.TestAquabasileaCourseBookerPersistenceConfig;
import org.awaitility.Duration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@SpringBootTest(classes = TestAquabasileaCourseBookerPersistenceConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AquabasileaCourseBookerInitializerTest {

   @Autowired
   private AquabasileaCourseBookerInitializer aquabasileaCourseBookerInitializer;

   private final AquabasileaCourseBookerHolder aquabasileaCourseBookerHolder = new AquabasileaCourseBookerHolder();

   @Autowired
   private PersistenceInitializer persistenceInitializer;
   @Autowired
   private WeeklyCoursesRepository weeklyCoursesRepository;
   @Autowired
   private StatisticsRepository statisticsRepository;
   @Autowired
   private UserConfigRepository userConfigRepository;

   @BeforeAll
   public void setup() {
      weeklyCoursesRepository.deleteAll();
      statisticsRepository.deleteAll();
      userConfigRepository.deleteAll();
   }

   @AfterAll
   public void cleanup() {
      weeklyCoursesRepository.deleteAll();
      statisticsRepository.deleteAll();
      userConfigRepository.deleteAll();
   }

   @Test
   void testInitializeAquabasileaCourseBookerInitializer() {
      // Given
      String userId = "1234";
      UserAddedEvent userAddedEvent = createUserAddedEvent("peter", userId);

      // When
      persistenceInitializer.initialize(userAddedEvent);// is needed otherwise the course-booker crashes since there is no weekly course
      aquabasileaCourseBookerInitializer.initialize(userAddedEvent);
      await().atMost(new Duration(30, TimeUnit.SECONDS)).until(() -> {
         AquabasileaCourseBooker aquabasileaCourseBooker = aquabasileaCourseBookerHolder.getForUserId(userId);
         return TextResources.INFO_TEXT_INIT.equals(aquabasileaCourseBooker.getInfoString4State());
      });

      // Then
      // success
   }

   private static UserAddedEvent createUserAddedEvent(String username, String userId) {
      char[] userPwd = "1234".toCharArray();
      return new UserAddedEvent(username, "123", userId, userPwd);
   }
}