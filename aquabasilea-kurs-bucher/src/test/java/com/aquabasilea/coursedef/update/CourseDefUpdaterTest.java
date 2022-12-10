package com.aquabasilea.coursedef.update;

import com.aquabasilea.model.course.CourseLocation;
import com.aquabasilea.model.course.coursedef.CourseDef;
import com.aquabasilea.model.course.coursedef.repository.CourseDefRepository;
import com.aquabasilea.model.course.coursedef.update.CourseDefUpdateDate;
import com.aquabasilea.model.course.coursedef.update.CourseDefUpdater;
import com.aquabasilea.model.course.coursedef.update.facade.CourseExtractorFacade;
import com.aquabasilea.model.statistics.Statistics;
import com.aquabasilea.model.statistics.repository.StatisticsRepository;
import com.aquabasilea.model.userconfig.UserConfig;
import com.aquabasilea.model.userconfig.repository.UserConfigRepository;
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
      CourseDefUpdater courseDefUpdater = new CourseDefUpdater(getCourseExtractorFacade(aquabasileaCourseExtractor), statisticsService, courseDefRepository, userConfigRepository);

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
   void startSchedulerAndUpdateImmediately_WithPreviousUpdateButToOld() {

      // Given
      CourseLocation courseLocation = CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA;
      LocalDateTime courseDate = LocalDateTime.of(2022, Month.JUNE, 1, 10, 15);
      String courseName = "Test";
      TestAquabasileaCourseExtractor aquabasileaCourseExtractor = createNewTestAquabasileaCourseExtractor(courseLocation, courseDate, courseName, 0, "karl");
      CourseDefUpdater courseDefUpdater = new CourseDefUpdater(getCourseExtractorFacade(aquabasileaCourseExtractor), statisticsService, courseDefRepository, userConfigRepository);

      // When
      courseDefUpdater.startScheduler(USER_ID_WITH_PREV_UPDATE);
      await().atMost(new Duration(5, TimeUnit.SECONDS)).until(() -> aquabasileaCourseExtractor.amountOfInvocations == 1);

      // Then
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
      CourseDefUpdater courseDefUpdater = new CourseDefUpdater(getCourseExtractorFacade(defaultAquabasileaCourse), statisticsService,
              courseDefRepository, userConfigRepository, courseDefUpdateDate);

      // When
      // start scheduler and execute update immediately, although the update is scheduled one day from one. But since there was no update at all, do it initially
      courseDefUpdater.startScheduler(USER_ID);
      await().atMost(new Duration(5, TimeUnit.SECONDS)).until(() -> nonNull(statisticsService.getStatisticsDto(USER_ID).getLastCourseDefUpdate()));

      // Then
      List<CourseDef> allCourseDefs = courseDefRepository.getAll();
      assertThat(allCourseDefs.size(), is(1));
      assertThat(allCourseDefs.get(0).courseName(), is(defaultAquabasileaCourse.courseName()));
      assertThat(allCourseDefs.get(0).courseDate(), is(defaultAquabasileaCourse.courseDate()));
   }

   @Test
   void startSchedulerScheduleNextUpdateSinceThereIsPreviousUpdate() {

      // Given
      this.statisticsService.setLastCourseDefUpdate(USER_ID, LocalDateTime.now());
      LocalDateTime now = LocalDateTime.now().plusDays(1);
      String localDateTimeAsString = DateUtil.getTimeAsString(now);
      String timeOfTheDayAsString = localDateTimeAsString.substring(localDateTimeAsString.indexOf(", ") + 1);
      CourseDefUpdateDate courseDefUpdateDate = new CourseDefUpdateDate(now.getDayOfWeek(), timeOfTheDayAsString);
      AquabasileaCourse defaultAquabasileaCourse = createDefaultAquabasileaCourse();
      CourseDefUpdater courseDefUpdater = new CourseDefUpdater(getCourseExtractorFacade(defaultAquabasileaCourse), statisticsService,
              courseDefRepository, userConfigRepository, courseDefUpdateDate);

      // When
      courseDefUpdater.startScheduler(USER_ID);

      // Then
      // no scheduler started, since there is already an update in the statistic-table
      List<CourseDef> allCourseDefs = courseDefRepository.getAll();
      assertThat(allCourseDefs.size(), is(0));
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
      return new CourseExtractorFacade(() -> courseLocations -> () -> List.of(defaultAquabasileaCourse), null);
   }

   private static CourseExtractorFacade getCourseExtractorFacade(TestAquabasileaCourseExtractor aquabasileaCourseExtractor) {
      return new CourseExtractorFacade(() -> aquabasileaCourseExtractor, null);
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