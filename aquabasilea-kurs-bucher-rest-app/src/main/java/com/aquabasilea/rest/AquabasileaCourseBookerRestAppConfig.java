package com.aquabasilea.rest;

import com.aquabasilea.alerting.consumer.impl.AlertSender;
import com.aquabasilea.coursebooker.AquabasileaCourseBooker;
import com.aquabasilea.coursedef.update.CourseDefUpdater;
import com.aquabasilea.model.course.coursedef.repository.CourseDefRepository;
import com.aquabasilea.model.course.coursedef.repository.mapping.CoursesDefEntityMapperImpl;
import com.aquabasilea.model.course.weeklycourses.repository.WeeklyCoursesRepository;
import com.aquabasilea.persistence.config.AquabasileaCourseBookerPersistenceConfig;
import com.aquabasilea.persistence.entity.statistic.StatisticsHelper;
import com.aquabasilea.rest.service.WeeklyCoursesService;
import com.aquabasilea.statistics.BookingStatisticsUpdater;
import com.aquabasilea.statistics.repository.StatisticsRepository;
import com.aquabasilea.web.extractcourses.impl.AquabasileaCourseExtractorImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;

import static com.aquabasilea.persistence.config.AquabasileaCourseBookerPersistenceConfig.WEEKLY_COURSES_REPOSITORY_BEAN;

@Configuration
@EnableAutoConfiguration
@Import(AquabasileaCourseBookerPersistenceConfig.class)
public class AquabasileaCourseBookerRestAppConfig {

   public static final String AQUABASILEA_COURSE_BOOKER_BEAN = "aquabasileaCourseBooker";
   public static final String COURSE_DEF_UPDATER_BEAN = "courseDefUpdater";
   public static final String STATISTICS_HELPER_BEAN = "statisticsHelper";

   private final AquabasileaCourseBookerSupplier aquabasileaCourseBookerSupplier = new AquabasileaCourseBookerSupplier();

   @Bean(STATISTICS_HELPER_BEAN)
   public StatisticsHelper getStatisticsHelper(@Autowired StatisticsRepository statisticsRepository) {
      return new StatisticsHelper(statisticsRepository);
   }

   @DependsOn({WEEKLY_COURSES_REPOSITORY_BEAN, STATISTICS_HELPER_BEAN})
   @Bean(name = AQUABASILEA_COURSE_BOOKER_BEAN)
   public AquabasileaCourseBooker getAquabasileaCourseBooker(@Autowired WeeklyCoursesRepository weeklyCoursesRepository,
                                                             @Autowired StatisticsHelper statisticsHelper) {
      return createAquabasileaCourseBooker(weeklyCoursesRepository, statisticsHelper);
   }

   @DependsOn(STATISTICS_HELPER_BEAN)
   @Bean(name = COURSE_DEF_UPDATER_BEAN)
   public CourseDefUpdater getCourseDefUpdater(@Autowired CourseDefRepository courseDefRepository, @Autowired StatisticsHelper statisticsHelper,
                                               @Autowired WeeklyCoursesService weeklyCoursesService) {
      CourseDefUpdater courseDefUpdater = new CourseDefUpdater(AquabasileaCourseExtractorImpl::createAndInitAquabasileaWebNavigator,
              statisticsHelper, courseDefRepository, new CoursesDefEntityMapperImpl());
      courseDefUpdater.addCourseDefUpdatedNotifier(weeklyCoursesService::updateCoursesAfterCourseDefUpdate);
      return courseDefUpdater;
   }

   private AquabasileaCourseBooker createAquabasileaCourseBooker(WeeklyCoursesRepository weeklyCoursesRepository, StatisticsHelper statisticsHelper) {
      AquabasileaCourseBooker aquabasileaCourseBooker = new AquabasileaCourseBooker(weeklyCoursesRepository, createAquabasileaCourseBookerThread());
      aquabasileaCourseBooker.addCourseBookingEndResultConsumer(new AlertSender());
      aquabasileaCourseBooker.addCourseBookingEndResultConsumer(new BookingStatisticsUpdater(statisticsHelper));
      aquabasileaCourseBookerSupplier.aquabasileaCourseBooker = aquabasileaCourseBooker;
      return aquabasileaCourseBooker;
   }

   private Thread createAquabasileaCourseBookerThread() {
      Runnable threadRunnable = () -> aquabasileaCourseBookerSupplier.aquabasileaCourseBooker.run();
      return new Thread(threadRunnable);
   }

   private static class AquabasileaCourseBookerSupplier {
      private AquabasileaCourseBooker aquabasileaCourseBooker;
   }
}

