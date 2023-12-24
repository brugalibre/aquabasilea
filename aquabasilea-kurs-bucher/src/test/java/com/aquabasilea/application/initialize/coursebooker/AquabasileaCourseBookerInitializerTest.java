package com.aquabasilea.application.initialize.coursebooker;

import com.aquabasilea.application.i18n.TextResources;
import com.aquabasilea.application.initialize.api.user.UserAddedEvent;
import com.aquabasilea.application.initialize.persistence.PersistenceInitializer;
import com.aquabasilea.domain.course.model.Course.CourseBuilder;
import com.aquabasilea.domain.course.model.WeeklyCourses;
import com.aquabasilea.domain.course.repository.WeeklyCoursesRepository;
import com.aquabasilea.domain.coursebooker.AquabasileaCourseBooker;
import com.aquabasilea.domain.coursebooker.AquabasileaCourseBookerHolder;
import com.aquabasilea.domain.coursebooker.config.AquabasileaCourseBookerConfig;
import com.aquabasilea.domain.courselocation.model.repository.CourseLocationRepository;
import com.aquabasilea.domain.statistics.model.repository.StatisticsRepository;
import com.aquabasilea.domain.userconfig.repository.UserConfigRepository;
import com.aquabasilea.persistence.config.TestAquabasileaCourseBookerPersistenceConfig;
import com.aquabasilea.util.DateUtil;
import com.brugalibre.domain.user.model.User;
import com.brugalibre.domain.user.repository.UserRepository;
import com.brugalibre.persistence.user.Role;
import org.awaitility.Duration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.aquabasilea.test.TestConstants.FITNESSPARK_GLATTPARK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(classes = TestAquabasileaCourseBookerPersistenceConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AquabasileaCourseBookerInitializerTest {

   @Autowired
   private AquabasileaCourseBookerInitializer aquabasileaCourseBookerInitializer;

   @Autowired
   private AquabasileaCourseBookerHolder aquabasileaCourseBookerHolder;

   @Autowired
   private CourseLocationRepository courseLocationRepository;

   @Autowired
   private PersistenceInitializer persistenceInitializer;

   @Autowired
   private WeeklyCoursesRepository weeklyCoursesRepository;

   @Autowired
   private StatisticsRepository statisticsRepository;

   @Autowired
   private UserConfigRepository userConfigRepository;

   @Autowired
   private UserRepository userRepository;

   private final AquabasileaCourseBookerConfig aquabasileaCourseBookerConfig = new AquabasileaCourseBookerConfig("config/test-aquabasilea-kurs-bucher-config.yml");

   @BeforeAll
   public void setup() {
      cleanup();
      courseLocationRepository.save(FITNESSPARK_GLATTPARK);
   }

   @AfterAll
   public void cleanup() {
      weeklyCoursesRepository.deleteAll();
      statisticsRepository.deleteAll();
      courseLocationRepository.deleteAll();
      userConfigRepository.deleteAll();
      userRepository.deleteAll();
   }

   @Test
   void testInitializeAquabasileaCourseBookerInitializer() {
      // Given
      LocalDate date = LocalDate.now().plusDays(2);
      LocalDateTime courseDate = LocalDateTime.of(date, LocalTime.now().plusHours(1));
      String courseName = "name";
      String username = "Peter";
      User user = userRepository.save(new User(null, username, "te", List.of(), List.of(Role.USER)));
      String userId = user.id();
      UserAddedEvent userAddedEvent = createUserAddedEvent(username, userId);
      String expectedStateMsg = buildExpectedStateMessage(courseDate, courseName);

      persistenceInitializer.initialize(userAddedEvent);// is needed otherwise the course-booker crashes since there is no weekly course
      // Add one course, so the coursebooker finds something and can be properly initialized
      WeeklyCourses weeklyCourses = weeklyCoursesRepository.getByUserId(userId);
      weeklyCourses.addCourse(CourseBuilder.builder()
              .withCourseLocation(courseLocationRepository.findByCenterId(FITNESSPARK_GLATTPARK.centerId()))
              .withCourseName(courseName)
              .withCourseInstructor("Heinz")
              .withHasCourseDef(true)
              .withCourseDate(courseDate)
              .build());
      weeklyCoursesRepository.save(weeklyCourses);

      // When
      aquabasileaCourseBookerInitializer.initialize(userAddedEvent);
      await().atMost(new Duration(20, TimeUnit.SECONDS)).until(() -> {
         AquabasileaCourseBooker aquabasileaCourseBooker = aquabasileaCourseBookerHolder.getForUserId(userId);
         return aquabasileaCourseBooker.isIdle();
      });

      // Then
      AquabasileaCourseBooker aquabasileaCourseBooker = aquabasileaCourseBookerHolder.getForUserId(userId);
      assertThat(aquabasileaCourseBooker.getInfoString4State()).isEqualTo(expectedStateMsg);
   }

   private String buildExpectedStateMessage(LocalDateTime courseDate, String courseName) {
      String courseDateAsString = DateUtil.toString(courseDate, Locale.GERMAN);
      int daysToBookCourseEarlier = aquabasileaCourseBookerConfig.getDaysToBookCourseEarlier();
      String dryRunTimeAsString = DateUtil.toString(courseDate.minusDays(daysToBookCourseEarlier)
              .minusHours(aquabasileaCourseBookerConfig.getDurationToStartDryRunEarlier().toHours()), Locale.GERMAN);
      return TextResources.INFO_TEXT_IDLE_BEFORE_DRY_RUN.formatted(courseName, courseDateAsString, dryRunTimeAsString);
   }

   @Test
   void testInitializeAquabasileaCourseBookerInitializer_NoCourses() {
      // Given
      String username = "Heinz";
      User user = userRepository.save(new User(null, username, "te", List.of(), List.of(Role.USER)));
      String userId = user.id();
      UserAddedEvent userAddedEvent = createUserAddedEvent(username, userId);
      persistenceInitializer.initialize(userAddedEvent);// is needed otherwise the course-booker crashes since there is no weekly course

      // When
      aquabasileaCourseBookerInitializer.initialize(userAddedEvent);
      await().atMost(new Duration(20, TimeUnit.SECONDS)).until(() -> {
         AquabasileaCourseBooker aquabasileaCourseBooker = aquabasileaCourseBookerHolder.getForUserId(userId);
         return TextResources.INFO_TEXT_APP_PAUSED.equals(aquabasileaCourseBooker.getInfoString4State());
      });

      // Then
      // success
   }

   private static UserAddedEvent createUserAddedEvent(String username, String userId) {
      char[] userPwd = "1234".toCharArray();
      return new UserAddedEvent(username, "123", userId, userPwd);
   }
}