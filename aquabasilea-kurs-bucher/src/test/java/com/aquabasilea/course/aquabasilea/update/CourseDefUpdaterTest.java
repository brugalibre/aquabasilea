package com.aquabasilea.course.aquabasilea.update;

import com.aquabasilea.course.CourseLocation;
import com.aquabasilea.course.aquabasilea.CourseDef;
import com.aquabasilea.course.aquabasilea.repository.CourseDefRepository;
import com.aquabasilea.course.aquabasilea.repository.mapping.CoursesDefEntityMapperImpl;
import com.aquabasilea.persistence.config.TestAquabasileaCourseBookerPersistenceConfig;
import com.aquabasilea.persistence.entity.statistic.StatisticsHelper;
import com.aquabasilea.statistics.repository.StatisticsRepository;
import com.aquabasilea.util.DateUtil;
import com.aquabasilea.web.extractcourses.AquabasileaCourseExtractor;
import com.aquabasilea.web.extractcourses.model.AquabasileaCourse;
import com.aquabasilea.web.extractcourses.model.ExtractedAquabasileaCourses;
import org.awaitility.Duration;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.nonNull;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(classes = TestAquabasileaCourseBookerPersistenceConfig.class)
class CourseDefUpdaterTest {

   @Autowired
   private CourseDefRepository courseDefRepository;

   @Autowired
   private StatisticsRepository statisticsRepository;

   private StatisticsHelper statisticsHelper;

   @BeforeEach
   public void setUp(){
      this.statisticsHelper = new StatisticsHelper(statisticsRepository);
      this.statisticsHelper.setLastCourseDefUpdate(null);
      this.courseDefRepository.deleteAll();
   }

   @AfterEach
   public void cleanUp(){
      this.statisticsRepository.deleteAll();
      this.courseDefRepository.deleteAll();
   }

   @Test
   void updateAquabasileaCourses() throws InterruptedException {

      // Given
      CourseLocation courseLocation = CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA;
      LocalDate courseDate = LocalDate.of(2022, Month.JUNE, 1);
      String timeOfTheDay = "10:15";
      String courseName = "Test";
      TestAquabasileaCourseExtractor aquabasileaCourseExtractor = createNewTestAquabasileaCourseExtractor(courseLocation, courseDate, timeOfTheDay, courseName, 50);
      CourseDefUpdater courseDefUpdater = new CourseDefUpdater(() -> aquabasileaCourseExtractor, statisticsHelper, courseDefRepository, new CoursesDefEntityMapperImpl());

      // When
      // Start 2 times, but it should only execute one time
      courseDefUpdater.updateAquabasileaCourses(List.of(courseLocation));
      Thread.sleep(10);// wait in order to trigger the scheduler-thread
      Thread.sleep(10);// give the ThreadScheduler time to start
      courseDefUpdater.updateAquabasileaCourses(List.of(courseLocation));
      await().atMost(new Duration(5, TimeUnit.SECONDS)).until(() -> aquabasileaCourseExtractor.amountOfInvocations > 0);

      // Then
      List<CourseDef> allCourseDefs = courseDefRepository.findAllCourseDefs();
      assertThat(allCourseDefs.size(), is(1));
      assertThat(aquabasileaCourseExtractor.amountOfInvocations, is(1));
      assertThat(allCourseDefs.get(0).courseName(), is(courseName));
      assertThat(allCourseDefs.get(0).courseLocation(), is(CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA));
      assertThat(allCourseDefs.get(0).courseDate(), is(courseDate));
      assertThat(allCourseDefs.get(0).timeOfTheDay(), is(timeOfTheDay));
   }

   @Test
   void startSchedulerAndUpdateImmediately_NoPrecedentUpdate() {

      // Given
      CourseLocation courseLocation = CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA;
      LocalDate courseDate = LocalDate.of(2022, Month.JUNE, 1);
      String timeOfTheDay = "10:15";
      String courseName = "Test";
      TestAquabasileaCourseExtractor aquabasileaCourseExtractor = createNewTestAquabasileaCourseExtractor(courseLocation, courseDate, timeOfTheDay, courseName, 0);
      CourseDefUpdater courseDefUpdater = new CourseDefUpdater(() -> aquabasileaCourseExtractor, statisticsHelper, courseDefRepository, new CoursesDefEntityMapperImpl());

      // When
      courseDefUpdater.startScheduler();

      // Then
      List<CourseDef> allCourseDefs = courseDefRepository.findAllCourseDefs();
      assertThat(aquabasileaCourseExtractor.amountOfInvocations, is(1));
      assertThat(allCourseDefs.size(), is(1));
      assertThat(allCourseDefs.get(0).courseName(), is(courseName));
      assertThat(allCourseDefs.get(0).courseLocation(), is(CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA));
      assertThat(allCourseDefs.get(0).courseDate(), is(courseDate));
      assertThat(allCourseDefs.get(0).timeOfTheDay(), is(timeOfTheDay));
   }

   @Test
   void startSchedulerAndStartUpdateImmediatelySinceThereIsNoPreviousUpdate()  {

      // Given
      LocalDateTime now = LocalDateTime.now().plusDays(1);
      String localDateTimeAsString = DateUtil.getTimeAsString(now);
      String timeOfTheDayAsString = localDateTimeAsString.substring(localDateTimeAsString.indexOf(", ") + 1);
      CourseDefUpdateDate courseDefUpdateDate = new CourseDefUpdateDate(now.getDayOfWeek(), timeOfTheDayAsString);
      AquabasileaCourse defaultAquabasileaCourse = createDefaultAquabasileaCourse();
      CourseDefUpdater courseDefUpdater = new CourseDefUpdater(() -> courseLocations -> () -> List.of(defaultAquabasileaCourse), statisticsHelper,
              courseDefRepository, new CoursesDefEntityMapperImpl(), "", courseDefUpdateDate);

      // When
      // start scheduler and execute update immediately, although the update is scheduled one day from one. But since there was no update at all, do it initially
      courseDefUpdater.startScheduler();
      await().atMost(new Duration(5, TimeUnit.SECONDS)).until(() -> nonNull(statisticsHelper.getStatisticsDto().getLastCourseDefUpdate()));

      // Then
      List<CourseDef> allCourseDefs = courseDefRepository.findAllCourseDefs();
      assertThat(allCourseDefs.size(), is(1));
      assertThat(allCourseDefs.get(0).courseName(), is(defaultAquabasileaCourse.courseName()));
      assertThat(allCourseDefs.get(0).courseDate(), is(defaultAquabasileaCourse.courseDate()));
      assertThat(allCourseDefs.get(0).timeOfTheDay(), is(defaultAquabasileaCourse.timeOfTheDay()));
   }

   @Test
   void startSchedulerScheduleNextUpdateSinceThereIsPreviousUpdate()  {

      // Given
      this.statisticsHelper.setLastCourseDefUpdate(LocalDateTime.now());
      LocalDateTime now = LocalDateTime.now().plusDays(1);
      String localDateTimeAsString = DateUtil.getTimeAsString(now);
      String timeOfTheDayAsString = localDateTimeAsString.substring(localDateTimeAsString.indexOf(", ") + 1);
      CourseDefUpdateDate courseDefUpdateDate = new CourseDefUpdateDate(now.getDayOfWeek(), timeOfTheDayAsString);
      AquabasileaCourse defaultAquabasileaCourse = createDefaultAquabasileaCourse();
      CourseDefUpdater courseDefUpdater = new CourseDefUpdater(() -> courseLocations -> () -> List.of(defaultAquabasileaCourse), statisticsHelper,
              courseDefRepository, new CoursesDefEntityMapperImpl(), "", courseDefUpdateDate);

      // When
      courseDefUpdater.startScheduler();

      // Then
      // no scheduler started, since there is already an update in the statistic-table
      List<CourseDef> allCourseDefs = courseDefRepository.findAllCourseDefs();
      assertThat(allCourseDefs.size(), is(0));
   }

   @NotNull
   private TestAquabasileaCourseExtractor createNewTestAquabasileaCourseExtractor(CourseLocation courseLocation, LocalDate courseDate, String timeOfTheDay, String courseName, long extractingDuration) {
      List<AquabasileaCourse> aquabasileaCourses = List.of(createAquabasileaCourse(courseLocation, courseDate, timeOfTheDay, courseName));
      return new TestAquabasileaCourseExtractor(aquabasileaCourses, extractingDuration);
   }

   @NotNull
   private static AquabasileaCourse createAquabasileaCourse(CourseLocation courseLocation, LocalDate courseDate, String timeOfTheDay, String courseName) {
      return new AquabasileaCourse(courseDate, timeOfTheDay, courseLocation.getWebCourseLocation(), courseName);
   }

   private static AquabasileaCourse createDefaultAquabasileaCourse() {
      return new AquabasileaCourse(LocalDate.of(2022, Month.JUNE, 5), "10:15", com.aquabasilea.web.model.CourseLocation.FITNESSPARK_HEUWAAGE, "test");
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
      public ExtractedAquabasileaCourses extractAquabasileaCourses(List<com.aquabasilea.web.model.CourseLocation> list) {
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