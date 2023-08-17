package com.aquabasilea.application.config;

import com.aquabasilea.domain.course.repository.WeeklyCoursesRepository;
import com.aquabasilea.domain.course.repository.impl.WeeklyCoursesRepositoryImpl;
import com.aquabasilea.domain.coursedef.model.repository.CourseDefRepository;
import com.aquabasilea.domain.coursedef.model.repository.impl.CourseDefRepositoryImpl;
import com.aquabasilea.domain.coursedef.update.CourseDefUpdater;
import com.aquabasilea.domain.coursedef.update.facade.CourseExtractorFacade;
import com.aquabasilea.domain.coursedef.update.service.CourseDefUpdaterService;
import com.aquabasilea.domain.statistics.model.repository.StatisticsRepository;
import com.aquabasilea.domain.statistics.model.repository.impl.StatisticsRepositoryImpl;
import com.aquabasilea.domain.statistics.service.CourseDefStatisticsUpdater;
import com.aquabasilea.domain.userconfig.repository.UserConfigRepository;
import com.aquabasilea.domain.userconfig.repository.impl.UserConfigRepositoryImpl;
import com.aquabasilea.persistence.coursedef.dao.CoursesDefDao;
import com.aquabasilea.persistence.courses.dao.WeeklyCoursesDao;
import com.aquabasilea.persistence.statistics.dao.StatisticsDao;
import com.aquabasilea.persistence.userconfig.dao.UserConfigDao;
import com.aquabasilea.service.courses.WeeklyCoursesService;
import com.aquabasilea.service.statistics.StatisticsService;
import com.aquabasilea.service.userconfig.UserConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;

import static com.aquabasilea.service.courses.WeeklyCoursesService.WEEKLY_COURSES_SERVICE;

@Configuration
@ComponentScan(basePackages = {"com.aquabasilea.application.security.service", "com.aquabasilea.application.initialize",
        "com.aquabasilea.persistence", "com.aquabasilea.service"})
@Import({AquabasileaCourseBookerPersistenceConfig.class})
public class AquabasileaCourseBookerAppConfig {

   /**
    * Name of the {@link WeeklyCoursesRepository}-Bean
    */
   public static final String WEEKLY_COURSES_REPOSITORY_BEAN = "weeklyCoursesRepository";
   public static final String USER_CONFIG_REPOSITORY_BEAN = "userConfigRepository";
   public static final String COURSE_DEF_REPOSITORY_BEAN = "courseDefRepository";
   public static final String STATISTICS_REPOSITORY_BEAN = "statisticsRepository";
   public static final String COURSE_DEF_UPDATER_SERVICE_BEAN = "courseDefUpdaterService";
   public static final String COURSE_DEF_UPDATER_BEAN = "courseDefUpdater";
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
              statisticsService::needsCourseDefUpdate, courseDefRepository, userConfigRepository);
      courseDefUpdater.addCourseDefUpdatedNotifier(onCourseDefsUpdatedContext -> weeklyCoursesService.updateCoursesAfterCourseDefUpdate(onCourseDefsUpdatedContext.userId(), onCourseDefsUpdatedContext.updatedCourseDefs()));
      CourseDefStatisticsUpdater courseDefStatisticsUpdater = new CourseDefStatisticsUpdater(statisticsService);
      courseDefUpdater.addCourseDefUpdatedNotifier(courseDefStatisticsUpdater);
      courseDefUpdater.addCourseDefStartedNotifier(courseDefStatisticsUpdater);
      return courseDefUpdater;
   }

   @DependsOn({COURSE_DEF_UPDATER_BEAN, USER_CONFIG_REPOSITORY_BEAN})
   @Bean(name = COURSE_DEF_UPDATER_SERVICE_BEAN)
   public CourseDefUpdaterService getCourseDefUpdaterService(@Autowired CourseDefUpdater courseDefUpdater,
                                                             @Autowired UserConfigRepository userConfigRepository) {
      return new CourseDefUpdaterService(courseDefUpdater, new UserConfigService(userConfigRepository));
   }
}

