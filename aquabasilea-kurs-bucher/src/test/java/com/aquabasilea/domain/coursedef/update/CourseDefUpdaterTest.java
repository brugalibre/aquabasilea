package com.aquabasilea.domain.coursedef.update;

import com.aquabasilea.application.security.model.AuthenticationContainer;
import com.aquabasilea.application.security.service.AuthenticationContainerService;
import com.aquabasilea.domain.coursebooker.states.booking.facade.apimigros.mapping.MigrosCourseMapper;
import com.aquabasilea.domain.courselocation.model.CourseLocation;
import com.aquabasilea.domain.coursebooker.states.booking.facade.apimigros.AuthenticationContainerRegistry;
import com.aquabasilea.domain.coursebooker.states.booking.facade.apimigros.MigrosApiCourseDefExtractor;
import com.aquabasilea.domain.coursedef.model.CourseDef;
import com.aquabasilea.domain.coursedef.model.repository.CourseDefRepository;
import com.aquabasilea.domain.courselocation.model.repository.CourseLocationRepository;
import com.aquabasilea.domain.statistics.model.Statistics;
import com.aquabasilea.domain.statistics.model.repository.StatisticsRepository;
import com.aquabasilea.domain.statistics.service.CourseDefStatisticsUpdater;
import com.aquabasilea.domain.userconfig.model.UserConfig;
import com.aquabasilea.domain.userconfig.repository.UserConfigRepository;
import com.aquabasilea.migrosapi.api.v1.model.getcourse.response.MigrosApiGetCoursesResponse;
import com.aquabasilea.migrosapi.api.v1.model.getcourse.response.MigrosCourse;
import com.aquabasilea.migrosapi.api.v1.service.MigrosApi;
import com.aquabasilea.persistence.config.TestAquabasileaCourseBookerPersistenceConfig;
import com.aquabasilea.service.statistics.StatisticsService;
import com.aquabasilea.util.DateUtil;
import org.awaitility.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.*;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.aquabasilea.test.TestConstants.*;
import static java.util.Objects.nonNull;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = TestAquabasileaCourseBookerPersistenceConfig.class)
class CourseDefUpdaterTest {

   private static final String USER_ID = UUID.randomUUID().toString();
   private static final String USER_ID_WITH_PREV_AND_TO_OLD_UPDATE = UUID.randomUUID().toString();
   private static final String USER_ID_WITH_PREV_AND_VALID_UPDATE = UUID.randomUUID().toString();

   @Autowired
   private CourseDefRepository courseDefRepository;

   @Autowired
   private CourseLocationRepository courseLocationRepository;

   @Autowired
   private StatisticsRepository statisticsRepository;

   @Autowired
   private UserConfigRepository userConfigRepository;

   private StatisticsService statisticsService;
   private CourseLocation aquabasileaFitnessCenter;

   @BeforeEach
   public void setUp() {
      cleanUp();
      courseLocationRepository.saveAll(List.of(MIGROS_FITNESSCENTER_AQUABASILEA, FITNESSPARK_GLATTPARK, FITNESSPARK_HEUWAAGE));
      aquabasileaFitnessCenter = courseLocationRepository.findByCenterId(MIGROS_FITNESSCENTER_AQUABASILEA.centerId());

      Statistics statistics = new Statistics(USER_ID, Clock.systemDefaultZone());
      statisticsRepository.save(statistics);
      Statistics statisticsForUserWithPrevUpdate = new Statistics(USER_ID_WITH_PREV_AND_TO_OLD_UPDATE, Clock.systemDefaultZone());
      statisticsRepository.save(statisticsForUserWithPrevUpdate);
      Statistics statisticsForUserWithPrevButValidUpdate = new Statistics(USER_ID_WITH_PREV_AND_VALID_UPDATE, Clock.systemDefaultZone());
      statisticsRepository.save(statisticsForUserWithPrevButValidUpdate);
      UserConfig userConfig = new UserConfig(USER_ID, List.of(aquabasileaFitnessCenter));
      userConfigRepository.save(userConfig);
      UserConfig userConfigForUserWithPrevUpdate = new UserConfig(USER_ID_WITH_PREV_AND_TO_OLD_UPDATE, List.of(aquabasileaFitnessCenter));
      userConfigRepository.save(userConfigForUserWithPrevUpdate);
      this.statisticsService = new StatisticsService(statisticsRepository);
      this.statisticsService.setLastCourseDefUpdate(USER_ID, null);
      LocalDateTime prevUpdate = LocalDateTime.now().minusDays(3);
      this.statisticsService.setLastCourseDefUpdate(USER_ID_WITH_PREV_AND_TO_OLD_UPDATE, prevUpdate);
      this.statisticsService.setLastCourseDefUpdate(USER_ID_WITH_PREV_AND_VALID_UPDATE, LocalDateTime.now().minusMinutes(5));
   }

   @AfterEach
   public void cleanUp() {
      this.statisticsRepository.deleteAll();
      this.courseDefRepository.deleteAll();
      this.userConfigRepository.deleteAll();
      this.courseLocationRepository.deleteAll();
   }

   @Test
   void updateMigrosCourses() throws InterruptedException {

      // Given
      LocalDateTime courseDate = LocalDateTime.of(2022, Month.JUNE, 1, 10, 15);
      String courseName = "Test";
      TestCourseDefExtractorFacade testCourseDefExtractorFacade = createTestCourseDefExtractorFacade(aquabasileaFitnessCenter, courseDate, courseName, "peter");
      testCourseDefExtractorFacade.extractingDuration = 2000;
      CourseDefUpdater courseDefUpdater = new CourseDefUpdater(testCourseDefExtractorFacade, statisticsService::needsCourseDefUpdate, courseDefRepository, userConfigRepository);
      addCourseDefStatisticsUpdater(courseDefUpdater);

      // When
      // Start 2 times, but it should only execute one time
      // Start scheduler and update courses the first time
      new Thread(() -> courseDefUpdater.startScheduler(USER_ID)).start();
      Thread.sleep(120);// wait in order to trigger the scheduler-thread and wait for it to start
      // update 2nd time, but since we are already updating -> we don't expect anything here
      new Thread(() -> courseDefUpdater.updateAquabasileaCourses(USER_ID)).start();
      await().atMost(new Duration(5, TimeUnit.SECONDS)).until(() -> testCourseDefExtractorFacade.amountOfInvocations > 0);

      // Then
      List<CourseDef> allCourseDefs = courseDefRepository.getAll();
      assertThat(allCourseDefs.size(), is(1));
      assertThat(testCourseDefExtractorFacade.amountOfInvocations, is(1));
      assertThat(allCourseDefs.get(0).courseName(), is(courseName));
      assertThat(allCourseDefs.get(0).courseLocation(), is(aquabasileaFitnessCenter));
      assertThat(allCourseDefs.get(0).courseDate(), is(courseDate));
   }

   @Test
   void startSchedulerAndUpdateImmediately_WithPreviousUpdateButToOld_UpdateNow() {

      // Given
      LocalDateTime updateTime = LocalDateTime.now().minusMinutes(10);

      // If right now is after 23:00 o'clock -> the next execution will be tomorrow @23:00
      LocalDateTime expectedNextCourseDefUpdate = updateTime;
      if (updateTime.toLocalTime().isBefore(LocalTime.of(23, 0, 0))) {
         expectedNextCourseDefUpdate = LocalDateTime.of(updateTime.toLocalDate().plusDays(1), updateTime.toLocalTime());
      }
      String updateTimeAsString = DateUtil.getTimeAsString(updateTime);
      String timeOfTheDayAsString = updateTimeAsString.substring(updateTimeAsString.indexOf(", ") + 1);
      CourseDefUpdateDate courseDefUpdateDate = new CourseDefUpdateDate(updateTime.getDayOfWeek(), timeOfTheDayAsString);
      LocalDateTime courseDate = LocalDateTime.of(2022, Month.JUNE, 1, 10, 15);
      String courseName = "Test";
      TestCourseDefExtractorFacade testCourseDefExtractorFacade = createTestCourseDefExtractorFacade(aquabasileaFitnessCenter, courseDate, courseName, "karl");
      CourseDefUpdater courseDefUpdater = new CourseDefUpdater(testCourseDefExtractorFacade, statisticsService::needsCourseDefUpdate, courseDefRepository, userConfigRepository, courseDefUpdateDate);
      addCourseDefStatisticsUpdater(courseDefUpdater);

      // When
      courseDefUpdater.startScheduler(USER_ID_WITH_PREV_AND_TO_OLD_UPDATE);
      await().atMost(new Duration(1, TimeUnit.MINUTES)).until(() -> testCourseDefExtractorFacade.amountOfInvocations == 1);

      // Then
      Statistics statistics = statisticsRepository.getByUserId(USER_ID_WITH_PREV_AND_TO_OLD_UPDATE);
      assertThat(statistics.getNextCourseDefUpdate().toLocalDate(), is(expectedNextCourseDefUpdate.toLocalDate()));
      assertThat(statistics.getNextCourseDefUpdate().toLocalTime().getHour(), is(expectedNextCourseDefUpdate.toLocalTime().getHour()));
      assertThat(statistics.getNextCourseDefUpdate().toLocalTime().getMinute(), is(expectedNextCourseDefUpdate.toLocalTime().getMinute()));
      List<CourseDef> allCourseDefs = courseDefRepository.getAll();
      assertThat(allCourseDefs.size(), is(1));
      assertThat(allCourseDefs.get(0).courseName(), is(courseName));
      assertThat(allCourseDefs.get(0).courseLocation(), is(aquabasileaFitnessCenter));
      assertThat(allCourseDefs.get(0).courseDate(), is(courseDate));
   }

   @Test
   void startSchedulerAndUpdateImmediately_WithPreviousUpdateButTooOld_UpdateAt11pm() {

      // Given
      LocalDateTime now = LocalDateTime.now();
      LocalDateTime expectedNextCourseDefUpdate = getExpectedNextCourseDefUpdate(now);
      LocalDateTime courseDate = LocalDateTime.of(2022, Month.JUNE, 1, 10, 15);
      String courseName = "Test";
      TestCourseDefExtractorFacade testCourseDefExtractorFacade = createTestCourseDefExtractorFacade(aquabasileaFitnessCenter, courseDate, courseName, "karl");
      CourseDefUpdater courseDefUpdater = new CourseDefUpdater(testCourseDefExtractorFacade, statisticsService::needsCourseDefUpdate, courseDefRepository, userConfigRepository);
      addCourseDefStatisticsUpdater(courseDefUpdater);

      // When
      courseDefUpdater.startScheduler(USER_ID_WITH_PREV_AND_VALID_UPDATE);
      await().atMost(new Duration(10, TimeUnit.SECONDS)).until(() -> courseDefUpdater.calcDelayUntilNextUpdate() != null);

      // Then
      Statistics statistics = statisticsRepository.getByUserId(USER_ID_WITH_PREV_AND_VALID_UPDATE);
      assertThat(statistics.getNextCourseDefUpdate().toLocalDate(), is(expectedNextCourseDefUpdate.toLocalDate()));
      assertThat(statistics.getNextCourseDefUpdate().toLocalTime().getHour(), is(expectedNextCourseDefUpdate.toLocalTime().getHour()));
      assertThat(statistics.getNextCourseDefUpdate().toLocalTime().getMinute(), is(expectedNextCourseDefUpdate.toLocalTime().getMinute()));
      assertThat(testCourseDefExtractorFacade.amountOfInvocations, is(0));
      // now verify if the calculated next delay matches the expected update date
      LocalDateTime calculatedUpdateDate = now.plusNanos(courseDefUpdater.calcDelayUntilNextUpdate().toNanos());
      assertThat(calculatedUpdateDate.getMinute(), is(expectedNextCourseDefUpdate.toLocalTime().getMinute()));
      assertThat(calculatedUpdateDate.getHour(), is(expectedNextCourseDefUpdate.toLocalTime().getHour()));
   }

   private static LocalDateTime getExpectedNextCourseDefUpdate(LocalDateTime now) {
      LocalDateTime expectedNextCourseDefUpdate;
      // If right now is before 23:00 o'clock -> the next execution will be today @23:00
      if (now.toLocalTime().isBefore(LocalTime.of(23, 0, 0))) {
         expectedNextCourseDefUpdate = LocalDateTime.of(now.toLocalDate(), LocalTime.of(23, 0, 0));
      } else {
         // If right now already after 23:00 o'clock -> the next execution will be tomorrow @23:00
         expectedNextCourseDefUpdate = LocalDateTime.of(now.toLocalDate().plusDays(1), LocalTime.of(23, 0, 0));
      }
      return expectedNextCourseDefUpdate;
   }

   @Test
   void startSchedulerAndStartUpdateImmediatelySinceThereIsNoPreviousUpdate() {

      // Given
      LocalDateTime now = LocalDateTime.now().plusDays(1);
      String localDateTimeAsString = DateUtil.getTimeAsString(now);
      String timeOfTheDayAsString = localDateTimeAsString.substring(localDateTimeAsString.indexOf(", ") + 1);
      CourseDefUpdateDate courseDefUpdateDate = new CourseDefUpdateDate(now.getDayOfWeek(), timeOfTheDayAsString);
      MigrosCourse defaultAquabasileaCourse = createDefaultMigrosCourse();
      CourseDefUpdater courseDefUpdater = new CourseDefUpdater(getCourseDefExtractorFacade(List.of(defaultAquabasileaCourse)), statisticsService::needsCourseDefUpdate,
              courseDefRepository, userConfigRepository, courseDefUpdateDate);
      addCourseDefStatisticsUpdater(courseDefUpdater);

      // When
      // start scheduler and execute update immediately, although the update is scheduled one day from one. But since there was no update at all, do it initially
      courseDefUpdater.startScheduler(USER_ID);
      await().atMost(new Duration(5, TimeUnit.SECONDS)).until(() -> nonNull(statisticsService.getStatisticsByUserId(USER_ID).getLastCourseDefUpdate()));

      // Then
      List<CourseDef> allCourseDefs = courseDefRepository.getAll();
      assertThat(allCourseDefs.size(), is(1));
      assertThat(allCourseDefs.get(0).courseName(), is(defaultAquabasileaCourse.courseName()));
      assertThat(allCourseDefs.get(0).courseDate(), is(defaultAquabasileaCourse.courseDate()));
   }

   @Test
   void startSchedulerScheduleNextUpdateSinceThereIsPreviousUpdate() {

      // Given
      LocalDateTime now = LocalDateTime.now();
      courseDefRepository.deleteAll();
      this.statisticsService.setLastCourseDefUpdate(USER_ID, now);
      LocalDateTime nowPlusOneDay = now.plusDays(1);
      LocalDateTime expectedNextCourseDefUpdate = nowPlusOneDay.plusMinutes(1);
      String localDateTimeAsString = DateUtil.getTimeAsString(nowPlusOneDay);
      CourseDefUpdateDate courseDefUpdateDate = new CourseDefUpdateDate(nowPlusOneDay.getDayOfWeek(), localDateTimeAsString);
      TestCourseDefExtractorFacade testCourseDefExtractorFacade = getCourseDefExtractorFacade(List.of(createDefaultMigrosCourse()));
      CourseDefUpdater courseDefUpdater = new CourseDefUpdater(testCourseDefExtractorFacade, statisticsService::needsCourseDefUpdate,
              courseDefRepository, userConfigRepository, courseDefUpdateDate);
      addCourseDefStatisticsUpdater(courseDefUpdater);

      // When
      courseDefUpdater.startScheduler(USER_ID);
      await().atMost(new Duration(70, TimeUnit.SECONDS)).until(() -> testCourseDefExtractorFacade.amountOfInvocations == 1);

      // Then
      // no scheduler started, since there is already an update in the statistic-table
      List<CourseDef> allCourseDefs = courseDefRepository.getAll();
      assertThat(allCourseDefs.size(), is(1));
      Statistics statistics = statisticsRepository.getByUserId(USER_ID);
      assertThat(statistics.getNextCourseDefUpdate().toLocalDate(), is(expectedNextCourseDefUpdate.toLocalDate()));
      assertThat(statistics.getNextCourseDefUpdate().toLocalTime().getHour(), is(expectedNextCourseDefUpdate.toLocalTime().getHour()));
      assertThat(statistics.getNextCourseDefUpdate().toLocalTime().getMinute(), is(expectedNextCourseDefUpdate.toLocalTime().getMinute()));
   }

   @Test
   void startSchedulerDontUpdateNowAndScheduleNextUpdateSinceThereIsPreviousUpdate() {

      // Given
      LocalDateTime lastUpdate = LocalDateTime.now().minusMinutes(30);
      courseDefRepository.deleteAll();
      this.statisticsService.setLastCourseDefUpdate(USER_ID, lastUpdate);
      LocalDateTime expectedNextCourseDefUpdate = lastUpdate.plusDays(1);
      String localDateTimeAsString = DateUtil.getTimeAsString(lastUpdate);
      CourseDefUpdateDate courseDefUpdateDate = new CourseDefUpdateDate(lastUpdate.getDayOfWeek(), localDateTimeAsString);
      TestCourseDefExtractorFacade testCourseDefExtractorFacade = getCourseDefExtractorFacade(List.of(createDefaultMigrosCourse()));
      CourseDefUpdater courseDefUpdater = new CourseDefUpdater(testCourseDefExtractorFacade, statisticsService::needsCourseDefUpdate,
              courseDefRepository, userConfigRepository, courseDefUpdateDate);
      addCourseDefStatisticsUpdater(courseDefUpdater);

      // When
      courseDefUpdater.startScheduler(USER_ID);

      // Then
      // no scheduler started, since there is already an update in the statistic-table
      List<CourseDef> allCourseDefs = courseDefRepository.getAll();
      assertThat(allCourseDefs.size(), is(0));
      Statistics statistics = statisticsRepository.getByUserId(USER_ID);
      assertThat(statistics.getNextCourseDefUpdate().toLocalDate(), is(expectedNextCourseDefUpdate.toLocalDate()));
      assertThat(statistics.getNextCourseDefUpdate().toLocalTime().getHour(), is(expectedNextCourseDefUpdate.toLocalTime().getHour()));
      assertThat(statistics.getNextCourseDefUpdate().toLocalTime().getMinute(), is(expectedNextCourseDefUpdate.toLocalTime().getMinute()));
   }

   private void addCourseDefStatisticsUpdater(CourseDefUpdater courseDefUpdater) {
      CourseDefStatisticsUpdater courseDefStatisticsUpdater = new CourseDefStatisticsUpdater(statisticsService);
      courseDefUpdater.addCourseDefUpdatedNotifier(courseDefStatisticsUpdater);
      courseDefUpdater.addCourseDefStartedNotifier(courseDefStatisticsUpdater);
   }

   private TestCourseDefExtractorFacade createTestCourseDefExtractorFacade(CourseLocation courseLocation,
                                                                           LocalDateTime courseDate,
                                                                           String courseName,
                                                                           String courseInstructor) {
      List<MigrosCourse> migrosCourses = List.of(createMigrosCourse(courseLocation, courseDate, courseName, courseInstructor));
      return getCourseDefExtractorFacade(migrosCourses);
   }

   private static MigrosCourse createMigrosCourse(CourseLocation courseLocation, LocalDateTime courseDate, String courseName, String courseInstructor) {
      return new MigrosCourse(courseDate, courseLocation.centerId(), courseName, courseInstructor, "234");
   }

   private static MigrosCourse createDefaultMigrosCourse() {
      LocalDateTime courseDate = LocalDateTime.of(LocalDate.of(2022, Month.JUNE, 5), LocalTime.of(10, 15));
      return new MigrosCourse(courseDate, FITNESSPARK_HEUWAAGE.centerId(), "test", "heinz", "2344");
   }

   private TestCourseDefExtractorFacade getCourseDefExtractorFacade(List<MigrosCourse> migrosCourses) {
      MigrosApi migrosApi = mockMigrosApi(migrosCourses);
      AuthenticationContainerService authenticationContainerService = mockAuthenticationContainerService();
      return new TestCourseDefExtractorFacade(migrosApi, authenticationContainerService, courseLocationRepository);
   }

   private static AuthenticationContainerService mockAuthenticationContainerService() {
      AuthenticationContainerService authenticationContainerService = mock(AuthenticationContainerService.class);
      when(authenticationContainerService.getAuthenticationContainer(any())).thenReturn(new AuthenticationContainer("Karls", "pw"::toCharArray));
      return authenticationContainerService;
   }

   private static MigrosApi mockMigrosApi(List<MigrosCourse> migrosCourses) {
      MigrosApi migrosApi = mock(MigrosApi.class);
      when(migrosApi.getCourses(any(), any())).thenReturn(new MigrosApiGetCoursesResponse(migrosCourses));
      return migrosApi;
   }

   private static class TestCourseDefExtractorFacade extends MigrosApiCourseDefExtractor {

      private int amountOfInvocations;
      private long extractingDuration;

      public TestCourseDefExtractorFacade(MigrosApi migrosApi, AuthenticationContainerService authenticationContainerService,
                                          CourseLocationRepository courseLocationRepository) {
         super(migrosApi, new AuthenticationContainerRegistry(authenticationContainerService), MigrosCourseMapper.of(courseLocationRepository));
         this.amountOfInvocations = 0;
         this.extractingDuration = 0;
      }

      @Override
      public List<CourseDef> getCourseDefs(String userId, List<CourseLocation> courseLocations) {
         try {
            Thread.sleep(extractingDuration);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
         List<CourseDef> courseDefs = super.getCourseDefs(userId, courseLocations);
         this.amountOfInvocations++;
         return courseDefs;
      }
   }
}