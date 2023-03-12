package com.aquabasilea.app.config;

import com.aquabasilea.coursebooker.model.course.weeklycourses.repository.WeeklyCoursesRepository;
import com.aquabasilea.coursebooker.model.course.weeklycourses.repository.impl.WeeklyCoursesRepositoryImpl;
import com.aquabasilea.coursebooker.model.statistics.repository.StatisticsRepository;
import com.aquabasilea.coursebooker.model.statistics.repository.impl.StatisticsRepositoryImpl;
import com.aquabasilea.coursebooker.model.userconfig.repository.UserConfigRepository;
import com.aquabasilea.coursebooker.model.userconfig.repository.impl.UserConfigRepositoryImpl;
import com.aquabasilea.coursebooker.persistence.course.weeklycourses.dao.WeeklyCoursesDao;
import com.aquabasilea.coursebooker.persistence.statistic.dao.StatisticsDao;
import com.aquabasilea.coursebooker.persistence.userconfig.dao.UserConfigDao;
import com.aquabasilea.coursebooker.service.statistics.StatisticsService;
import com.aquabasilea.coursebooker.service.userconfig.UserConfigService;
import com.aquabasilea.coursebooker.service.weeklycourses.WeeklyCoursesService;
import com.aquabasilea.coursedef.model.repository.CourseDefRepository;
import com.aquabasilea.coursedef.model.repository.impl.CourseDefRepositoryImpl;
import com.aquabasilea.coursedef.persistence.dao.CoursesDefDao;
import com.aquabasilea.coursedef.service.CourseDefUpdaterService;
import com.aquabasilea.coursedef.update.CourseDefUpdater;
import com.aquabasilea.coursedef.update.facade.CourseExtractorFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;

import static com.aquabasilea.coursebooker.service.weeklycourses.WeeklyCoursesService.WEEKLY_COURSES_SERVICE;

@Configuration
@ComponentScan(basePackages = {"com.aquabasilea.security.service", "com.aquabasilea.app.initialize", "com.aquabasilea.coursebooker"})
@Import({AquabasileaCourseBookerPersistenceConfig.class})
public class AquabasileaCourseBookerAppConfig {

   /**
    * Name of the {@link WeeklyCoursesRepository}-Bean
    */
   public static final String WEEKLY_COURSES_REPOSITORY_BEAN = "weeklyCoursesRepository";
   public static final String WEEKLY_COURSES_SERVICE_BEAN = "weeklyCoursesService";
   public static final String USER_CONFIG_REPOSITORY_BEAN = "userConfigRepository";
   public static final String COURSE_DEF_REPOSITORY_BEAN = "courseDefRepository";
   public static final String STATISTICS_REPOSITORY_BEAN = "statisticsRepository";
   public static final String COURSE_DEF_UPDATER_SERVICE_BEAN = "courseDefUpdaterService";
   public static final String COURSE_DEF_UPDATER_BEAN = "courseDefService";
   public static final String STATISTICS_SERVICE_BEAN = "statisticsService";

   @Bean(name = WEEKLY_COURSES_REPOSITORY_BEAN)
   public WeeklyCoursesRepository getWeeklyCoursesRepository(@Autowired WeeklyCoursesDao weeklyCoursesDao) {
      return new WeeklyCoursesRepositoryImpl(weeklyCoursesDao);
   }

   @Bean(name = COURSE_DEF_REPOSITORY_BEAN)
   public CourseDefRepository getCourseDefRepository(@Autowired CoursesDefDao coursesDefDao) {
      return new CourseDefRepositoryImpl(coursesDefDao);
   }

   @Bean(name = STATISTICS_REPOSITORY_BEAN)
   public StatisticsRepository getStatisticsRepository(@Autowired StatisticsDao statisticsDao) {
      return new StatisticsRepositoryImpl(statisticsDao);
   }

   @Bean(name = USER_CONFIG_REPOSITORY_BEAN)
   public UserConfigRepository getUserConfigRepository(@Autowired UserConfigDao userConfigDao) {
      return new UserConfigRepositoryImpl(userConfigDao);
   }

   @DependsOn({STATISTICS_SERVICE_BEAN, USER_CONFIG_REPOSITORY_BEAN, COURSE_DEF_REPOSITORY_BEAN, WEEKLY_COURSES_SERVICE})
   @Bean(name = COURSE_DEF_UPDATER_BEAN)
   public CourseDefUpdater getCourseDefUpdater(@Autowired CourseDefRepository courseDefRepository,
                                               @Autowired StatisticsService statisticsService,
                                               @Autowired UserConfigRepository userConfigRepository,
                                               @Autowired WeeklyCoursesService weeklyCoursesService) {
      CourseDefUpdater courseDefUpdater = new CourseDefUpdater(CourseExtractorFacade.getCourseExtractorFacade(),
              statisticsService, courseDefRepository, userConfigRepository);
      courseDefUpdater.addCourseDefUpdatedNotifier(weeklyCoursesService::updateCoursesAfterCourseDefUpdate);
      return courseDefUpdater;
   }

   @DependsOn({COURSE_DEF_UPDATER_BEAN, USER_CONFIG_REPOSITORY_BEAN, COURSE_DEF_REPOSITORY_BEAN})
   @Bean(name = COURSE_DEF_UPDATER_SERVICE_BEAN)
   public CourseDefUpdaterService getCourseDefUpdaterService(@Autowired CourseDefUpdater courseDefUpdater,
                                                             @Autowired CourseDefRepository courseDefRepository,
                                                             @Autowired UserConfigRepository userConfigRepository) {
      return new CourseDefUpdaterService(courseDefUpdater, courseDefRepository, new UserConfigService(userConfigRepository));
   }
}

