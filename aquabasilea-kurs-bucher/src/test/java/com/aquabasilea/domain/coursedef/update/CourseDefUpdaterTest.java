package com.aquabasilea.domain.coursedef.update;

import com.aquabasilea.domain.course.model.CourseLocation;
import com.aquabasilea.domain.coursebooker.config.AquabasileaCourseBookerConfig;
import com.aquabasilea.domain.coursedef.model.CourseDef;
import com.aquabasilea.domain.coursedef.model.repository.CourseDefRepository;
import com.aquabasilea.domain.coursedef.update.facade.CourseDefExtractorType;
import com.aquabasilea.domain.coursedef.update.facade.CourseExtractorFacade;
import com.aquabasilea.domain.statistics.model.Statistics;
import com.aquabasilea.domain.statistics.model.repository.StatisticsRepository;
import com.aquabasilea.domain.statistics.service.CourseDefStatisticsUpdater;
import com.aquabasilea.domain.userconfig.model.UserConfig;
import com.aquabasilea.domain.userconfig.repository.UserConfigRepository;
import com.aquabasilea.persistence.config.TestAquabasileaCourseBookerPersistenceConfig;
import com.aquabasilea.service.statistics.StatisticsService;
import com.aquabasilea.util.DateUtil;
import com.aquabasilea.web.extractcourses.AquabasileaCourseExtractor;
import com.aquabasilea.web.extractcourses.model.AquabasileaCourse;
import com.aquabasilea.web.extractcourses.model.ExtractedAquabasileaCourses;
import org.awaitility.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.nonNull;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = TestAquabasileaCourseBookerPersistenceConfig.class)
class CourseDefUpdaterTest {

   private static final String USER_ID = UUID.randomUUID().toString();
   private static final String USER_ID_WITH_PREV_UPDATE = UUID.randomUUID().toString();

   @Autowired
   private CourseDefRepository courseDefRepository;

   @Autowired
   private StatisticsRepository statisticsRepository;

   @Autowired
   private UserConfigRepository userConfigRepository;

   private StatisticsService statisticsService;

   @BeforeEach
   public void setUp() {
      Statistics statistics = new Statistics(USER_ID);
      statisticsRepository.save(statistics);
      Statistics statisticsForUserWithPrevUpdate = new Statistics(USER_ID_WITH_PREV_UPDATE);
      statisticsRepository.save(statisticsForUserWithPrevUpdate);
      UserConfig userConfig = new UserConfig(USER_ID, List.of(CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA));
      userConfigRepository.save(userConfig);
      UserConfig userConfigForUserWithPrevUpdate = new UserConfig(USER_ID_WITH_PREV_UPDATE, List.of(CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA));
      userConfigRepository.save(userConfigForUserWithPrevUpdate);
      this.statisticsService = new StatisticsService(statisticsRepository);
      this.statisticsService.setLastCourseDefUpdate(USER_ID, null);
      LocalDateTime prevUpdate = LocalDateTime.now().minusDays(3);
      this.statisticsService.setLastCourseDefUpdate(USER_ID_WITH_PREV_UPDATE, prevUpdate);
   }

   @AfterEach
   public void cleanUp() {
      this.statisticsRepository.deleteAll();
      this.courseDefRepository.deleteAll();
      this.userConfigRepository.deleteAll();
   }

   @Test
   void updateAquabasileaCourses() throws InterruptedException {

      // Given
      CourseLocation courseLocation = CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA;
      LocalDateTime courseDate = LocalDateTime.of(2022, Month.JUNE, 1, 10, 15);
      String courseName = "Test";
      TestAquabasileaCourseExtractor aquabasileaCourseExtractor = createNewTestAquabasileaCourseExtractor(courseLocation, courseDate, courseName, 50, "peter");
      CourseDefUpdater courseDefUpdater = new CourseDefUpdater(getCourseExtractorFacade(aquabasileaCourseExtractor), statisticsService::needsCourseDefUpdate, courseDefRepository, userConfigRepository);
      addCourseDefStatisticsUpdater(courseDefUpdater);

      // When
      // Start 2 times, but it should only execute one time
      courseDefUpdater.updateAquabasileaCourses(USER_ID);
      Thread.sleep(10);// wait in order to trigger the scheduler-thread
      Thread.sleep(10);// give the ThreadScheduler time to start
      courseDefUpdater.updateAquabasileaCourses(USER_ID);
      await().atMost(new Duration(5, TimeUnit.SECONDS)).until(() -> aquabasileaCourseExtractor.amountOfInvocations > 0);

      // Then
      List<CourseDef> allCourseDefs = courseDefRepository.getAll();
      assertThat(allCourseDefs.size(), is(1));
      assertThat(aquabasileaCourseExtractor.amountOfInvocations, is(1));
      assertThat(allCourseDefs.get(0).courseName(), is(courseName));
      assertThat(allCourseDefs.get(0).courseLocation(), is(CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA));
      assertThat(allCourseDefs.get(0).courseDate(), is(courseDate));
   }

   @Test
   void startSchedulerAndUpdateImmediately_WithPreviousUpdateButToOld_UpdateNow() {

      // Given
      LocalDateTime updateTime = LocalDateTime.now();

      // If right now is after 23:00 o'clock -> the next execution will be tomorrow @23:00
      LocalDateTime expectedNextCourseDefUpdate = updateTime;
      if (updateTime.toLocalTime().compareTo(LocalTime.of(23, 0, 0)) < 0) {
         expectedNextCourseDefUpdate = LocalDateTime.of(updateTime.toLocalDate().plusDays(1), updateTime.toLocalTime());
      }
      String updateTimeAsString = DateUtil.getTimeAsString(updateTime);
      String timeOfTheDayAsString = updateTimeAsString.substring(updateTimeAsString.indexOf(", ") + 1);
      CourseDefUpdateDate courseDefUpdateDate = new CourseDefUpdateDate(updateTime.getDayOfWeek(), timeOfTheDayAsString);
      CourseLocation courseLocation = CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA;
      LocalDateTime courseDate = LocalDateTime.of(2022, Month.JUNE, 1, 10, 15);
      String courseName = "Test";
      TestAquabasileaCourseExtractor aquabasileaCourseExtractor = createNewTestAquabasileaCourseExtractor(courseLocation, courseDate, courseName, 0, "karl");
      CourseDefUpdater courseDefUpdater = new CourseDefUpdater(getCourseExtractorFacade(aquabasileaCourseExtractor), statisticsService::needsCourseDefUpdate, courseDefRepository, userConfigRepository, courseDefUpdateDate);
      addCourseDefStatisticsUpdater(courseDefUpdater);

      // When
      courseDefUpdater.startScheduler(USER_ID_WITH_PREV_UPDATE);
      await().atMost(new Duration(5, TimeUnit.MINUTES)).until(() -> aquabasileaCourseExtractor.amountOfInvocations == 1);

      // Then
      Statistics statistics = statisticsRepository.getByUserId(USER_ID_WITH_PREV_UPDATE);
      assertThat(statistics.getNextCourseDefUpdate().toLocalDate(), is(expectedNextCourseDefUpdate.toLocalDate()));
      assertThat(statistics.getNextCourseDefUpdate().toLocalTime().getHour(), is(expectedNextCourseDefUpdate.toLocalTime().getHour()));
      assertThat(statistics.getNextCourseDefUpdate().toLocalTime().getMinute(), is(expectedNextCourseDefUpdate.toLocalTime().getMinute()));
      List<CourseDef> allCourseDefs = courseDefRepository.getAll();
      assertThat(allCourseDefs.size(), is(1));
      assertThat(allCourseDefs.get(0).courseName(), is(courseName));
      assertThat(allCourseDefs.get(0).courseLocation(), is(CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA));
      assertThat(allCourseDefs.get(0).courseDate(), is(courseDate));
   }

   @Test
   void startSchedulerAndUpdateImmediately_WithPreviousUpdateButTooOld_UpdateAt11pm() {

      // Given
      LocalDateTime now = LocalDateTime.now();
      LocalDateTime expectedNextCourseDefUpdate;
      // If right now is before 23:00 o'clock -> the next execution will be today @23:00
      if (now.toLocalTime().compareTo(LocalTime.of(23, 0, 0)) < 0) {
         expectedNextCourseDefUpdate = LocalDateTime.of(now.toLocalDate(), LocalTime.of(23, 0, 0));
      } else {
         // If right now already after 23:00 o'clock -> the next execution will be tomorrow @23:00
         expectedNextCourseDefUpdate = LocalDateTime.of(now.toLocalDate().plusDays(1), LocalTime.of(23, 0, 0));
      }
      CourseLocation courseLocation = CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA;
      LocalDateTime courseDate = LocalDateTime.of(2022, Month.JUNE, 1, 10, 15);
      String courseName = "Test";
      TestAquabasileaCourseExtractor aquabasileaCourseExtractor = createNewTestAquabasileaCourseExtractor(courseLocation, courseDate, courseName, 0, "karl");
      CourseDefUpdater courseDefUpdater = new CourseDefUpdater(getCourseExtractorFacade(aquabasileaCourseExtractor), statisticsService::needsCourseDefUpdate, courseDefRepository, userConfigRepository);
      addCourseDefStatisticsUpdater(courseDefUpdater);

      // When
      courseDefUpdater.startScheduler(USER_ID_WITH_PREV_UPDATE);
      await().atMost(new Duration(5, TimeUnit.SECONDS)).until(() -> aquabasileaCourseExtractor.amountOfInvocations == 1);

      // Then
      Statistics statistics = statisticsRepository.getByUserId(USER_ID_WITH_PREV_UPDATE);
      assertThat(statistics.getNextCourseDefUpdate().toLocalDate(), is(expectedNextCourseDefUpdate.toLocalDate()));
      assertThat(statistics.getNextCourseDefUpdate().toLocalTime().getHour(), is(expectedNextCourseDefUpdate.toLocalTime().getHour()));
      assertThat(statistics.getNextCourseDefUpdate().toLocalTime().getMinute(), is(expectedNextCourseDefUpdate.toLocalTime().getMinute()));
      List<CourseDef> allCourseDefs = courseDefRepository.getAll();
      assertThat(allCourseDefs.size(), is(1));
      assertThat(allCourseDefs.get(0).courseName(), is(courseName));
      assertThat(allCourseDefs.get(0).courseLocation(), is(CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA));
      assertThat(allCourseDefs.get(0).courseDate(), is(courseDate));
   }

   @Test
   void startSchedulerAndStartUpdateImmediatelySinceThereIsNoPreviousUpdate() {

      // Given
      LocalDateTime now = LocalDateTime.now().plusDays(1);
      String localDateTimeAsString = DateUtil.getTimeAsString(now);
      String timeOfTheDayAsString = localDateTimeAsString.substring(localDateTimeAsString.indexOf(", ") + 1);
      CourseDefUpdateDate courseDefUpdateDate = new CourseDefUpdateDate(now.getDayOfWeek(), timeOfTheDayAsString);
      AquabasileaCourse defaultAquabasileaCourse = createDefaultAquabasileaCourse();
      CourseDefUpdater courseDefUpdater = new CourseDefUpdater(getCourseExtractorFacade(defaultAquabasileaCourse), statisticsService::needsCourseDefUpdate,
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
      TestAquabasileaCourseExtractor aquabasileaCourseExtractor = new TestAquabasileaCourseExtractor(List.of(createDefaultAquabasileaCourse()), 0);
      CourseDefUpdater courseDefUpdater = new CourseDefUpdater(getCourseExtractorFacade(aquabasileaCourseExtractor), statisticsService::needsCourseDefUpdate,
              courseDefRepository, userConfigRepository, courseDefUpdateDate);
      addCourseDefStatisticsUpdater(courseDefUpdater);

      // When
      courseDefUpdater.startScheduler(USER_ID);
      await().atMost(new Duration(70, TimeUnit.SECONDS)).until(() -> aquabasileaCourseExtractor.amountOfInvocations == 1);

      // Then
      // no scheduler started, since there is already an update in the statistic-table
      List<CourseDef> allCourseDefs = courseDefRepository.getAll();
      assertThat(allCourseDefs.size(), is(1));
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

   private TestAquabasileaCourseExtractor createNewTestAquabasileaCourseExtractor(CourseLocation courseLocation,
                                                                                  LocalDateTime courseDate,
                                                                                  String courseName, long extractingDuration,
                                                                                  String courseInstructor) {
      List<AquabasileaCourse> aquabasileaCourses = List.of(createAquabasileaCourse(courseLocation, courseDate, courseName, courseInstructor));
      return new TestAquabasileaCourseExtractor(aquabasileaCourses, extractingDuration);
   }

   private static AquabasileaCourse createAquabasileaCourse(CourseLocation courseLocation, LocalDateTime courseDate, String courseName, String courseInstructor) {
      return new AquabasileaCourse(courseDate, courseLocation.getCourseLocationName(), courseName, courseInstructor);
   }

   private static AquabasileaCourse createDefaultAquabasileaCourse() {
      LocalDateTime courseDate = LocalDateTime.of(LocalDate.of(2022, Month.JUNE, 5), LocalTime.of(10, 15));
      return new AquabasileaCourse(courseDate, CourseLocation.FITNESSPARK_HEUWAAGE.getCourseLocationName(), "test", "heinz");
   }

   private static CourseExtractorFacade getCourseExtractorFacade(AquabasileaCourse defaultAquabasileaCourse) {
      return getCourseExtractorFacade(courseLocations -> () -> List.of(defaultAquabasileaCourse));
   }

   private static CourseExtractorFacade getCourseExtractorFacade(AquabasileaCourseExtractor aquabasileaCourseExtractor) {
      AquabasileaCourseBookerConfig config = mock(AquabasileaCourseBookerConfig.class);
      when(config.refresh()).thenReturn(config);
      when(config.getCourseDefExtractorType()).thenReturn(CourseDefExtractorType.AQUABASILEA_WEB);
      return new CourseExtractorFacade(() -> aquabasileaCourseExtractor, null, config);
   }

   private static class TestAquabasileaCourseExtractor implements AquabasileaCourseExtractor {

      private final long extractingDuration;
      private final List<AquabasileaCourse> extractedAquabasileaCourses;
      private int amountOfInvocations;

      public TestAquabasileaCourseExtractor(List<AquabasileaCourse> extractedAquabasileaCourses, long extractingDuration) {
         this.extractedAquabasileaCourses = extractedAquabasileaCourses;
         this.extractingDuration = extractingDuration;
         this.amountOfInvocations = 0;
      }

      @Override
      public ExtractedAquabasileaCourses extractAquabasileaCourses(List<String> list) {
         try {
            Thread.sleep(extractingDuration);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
         this.amountOfInvocations++;
         return () -> extractedAquabasileaCourses;
      }
   }
}