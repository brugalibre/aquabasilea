package com.aquabasilea.application.config;

import com.aquabasilea.application.security.service.AuthenticationContainerService;
import com.aquabasilea.domain.course.repository.WeeklyCoursesRepository;
import com.aquabasilea.domain.course.repository.impl.WeeklyCoursesRepositoryImpl;
import com.aquabasilea.domain.coursebooker.AquabasileaCourseBookerHolder;
import com.aquabasilea.domain.coursebooker.states.booking.facade.CourseBookerFacadeFactory;
import com.aquabasilea.domain.coursebooker.states.booking.facade.CourseDefExtractorFacade;
import com.aquabasilea.domain.coursebooker.states.booking.facade.apimigros.AuthenticationContainerRegistry;
import com.aquabasilea.domain.coursedef.model.repository.CourseDefRepository;
import com.aquabasilea.domain.coursedef.model.repository.impl.CourseDefRepositoryImpl;
import com.aquabasilea.domain.coursedef.update.CourseDefUpdater;
import com.aquabasilea.domain.coursedef.update.service.CourseDefUpdaterService;
import com.aquabasilea.domain.courselocation.model.repository.CourseLocationRepository;
import com.aquabasilea.domain.courselocation.model.repository.impl.CourseLocationRepositoryImpl;
import com.aquabasilea.domain.statistics.model.repository.StatisticsRepository;
import com.aquabasilea.domain.statistics.model.repository.impl.StatisticsRepositoryImpl;
import com.aquabasilea.domain.statistics.service.CourseDefStatisticsUpdater;
import com.aquabasilea.domain.userconfig.repository.UserConfigRepository;
import com.aquabasilea.domain.userconfig.repository.impl.UserConfigRepositoryImpl;
import com.aquabasilea.migrosapi.api.service.configuration.MigrosApiBookStrategy;
import com.aquabasilea.migrosapi.api.service.configuration.ServiceConfiguration;
import com.aquabasilea.persistence.coursedef.dao.CoursesDefDao;
import com.aquabasilea.persistence.courselocation.dao.CourseLocationDao;
import com.aquabasilea.persistence.courses.dao.WeeklyCoursesDao;
import com.aquabasilea.persistence.statistics.dao.StatisticsDao;
import com.aquabasilea.persistence.userconfig.dao.UserConfigDao;
import com.aquabasilea.service.coursebooker.migros.MigrosApiProvider;
import com.aquabasilea.service.courselocation.CourseLocationCache;
import com.aquabasilea.service.courses.WeeklyCoursesService;
import com.aquabasilea.service.statistics.StatisticsService;
import com.aquabasilea.service.userconfig.UserConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

import static com.aquabasilea.service.courses.WeeklyCoursesService.WEEKLY_COURSES_SERVICE;

@Configuration
@ComponentScan(basePackages = {"com.aquabasilea.application.initialize",
        "com.aquabasilea.notification", "com.aquabasilea.persistence", "com.aquabasilea.service"})
@Import({AquabasileaCourseBookerPersistenceConfig.class, HealthCheckConfig.class})
public class AquabasileaCourseBookerAppConfig {

   /**
    * Name of the {@link WeeklyCoursesRepository}-Bean
    */
   public static final String WEEKLY_COURSES_REPOSITORY_BEAN = "weeklyCoursesRepository";
   public static final String USER_CONFIG_REPOSITORY_BEAN = "userConfigRepository";
   public static final String COURSE_DEF_REPOSITORY_BEAN = "courseDefRepository";
   public static final String STATISTICS_REPOSITORY_BEAN = "statisticsRepository";
   public static final String COURSE_DEF_UPDATER_SERVICE_BEAN = "courseDefUpdaterService";
   public static final String COURSE_DEF_EXTRACTOR_FACADE = "courseDefExtractorFacade";
   public static final String COURSE_BOOKER_FACADE = "courseBookerFacade";
   public static final String COURSE_DEF_UPDATER_BEAN = "courseDefUpdater";
   public static final String STATISTICS_SERVICE_BEAN = "statisticsService";
   public static final String CONFIG_YAML_FILE_PATHS_BEAN = "configYamlFilePaths";

   @Bean
   public AuthenticationContainerRegistry getAuthenticationContainerRegistry(AuthenticationContainerService authenticationContainerService) {
      return new AuthenticationContainerRegistry(authenticationContainerService);
   }

   @Bean
   public ServiceConfiguration getServiceConfiguration() {
      return ServiceConfiguration.of(MigrosApiBookStrategy.RETRY);
   }

   @Bean
   public AquabasileaCourseBookerHolder getAquabasileaCourseBookerHolder() {
      return new AquabasileaCourseBookerHolder();
   }

   @Bean(name = COURSE_BOOKER_FACADE)
   public CourseBookerFacadeFactory getCourseBookerFacadeFactory(@Autowired MigrosApiProvider migrosApiProvider) {
      return new CourseBookerFacadeFactory(migrosApiProvider);
   }

   @Bean(name = CONFIG_YAML_FILE_PATHS_BEAN)
   public ConfigYamlFilePaths getConfigYamlFilePaths(@Value("${application.configuration.course-booker-config}")
                                                     String courseBookerConfigFilePath) {
      return new ConfigYamlFilePaths(courseBookerConfigFilePath);
   }

   @Bean(name = WEEKLY_COURSES_REPOSITORY_BEAN)
   @DependsOn(CONFIG_YAML_FILE_PATHS_BEAN)
   public WeeklyCoursesRepository getWeeklyCoursesRepository(@Autowired WeeklyCoursesDao weeklyCoursesDao) {
      return new WeeklyCoursesRepositoryImpl(weeklyCoursesDao);
   }

   @Bean
   public CourseLocationRepository getCourseLocationRepository(@Autowired CourseLocationDao courseLocationDao) {
      return new CourseLocationRepositoryImpl(courseLocationDao);
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
   public UserConfigRepository getUserConfigRepository(@Autowired UserConfigDao userConfigDao,
                                                       @Autowired CourseLocationCache courseLocationCache) {
      return new UserConfigRepositoryImpl(userConfigDao, courseLocationCache);
   }

   @Bean(name = COURSE_DEF_EXTRACTOR_FACADE)
   public CourseDefExtractorFacade getCourseDefExtractorFacade(@Autowired MigrosApiProvider migrosApiProvider) {
      return migrosApiProvider.getMigrosApiCourseDefExtractorFacade();
   }

   @DependsOn({STATISTICS_SERVICE_BEAN, USER_CONFIG_REPOSITORY_BEAN, COURSE_DEF_REPOSITORY_BEAN, WEEKLY_COURSES_SERVICE, COURSE_DEF_EXTRACTOR_FACADE})
   @Bean(name = COURSE_DEF_UPDATER_BEAN)
   public CourseDefUpdater getCourseDefUpdater(@Autowired CourseDefRepository courseDefRepository,
                                               @Autowired CourseDefExtractorFacade courseDefExtractorFacade,
                                               @Autowired StatisticsService statisticsService,
                                               @Autowired UserConfigRepository userConfigRepository,
                                               @Autowired WeeklyCoursesService weeklyCoursesService) {
      CourseDefUpdater courseDefUpdater = new CourseDefUpdater(courseDefExtractorFacade, statisticsService::needsCourseDefUpdate,
              courseDefRepository, userConfigRepository);
      courseDefUpdater.addCourseDefUpdatedNotifier(onCourseDefsUpdatedContext -> weeklyCoursesService.updateCoursesAfterCourseDefUpdate(onCourseDefsUpdatedContext.userId(), onCourseDefsUpdatedContext.updatedCourseDefs()));
      courseDefUpdater.addCourseDefUpdatedNotifier(new CourseDefStatisticsUpdater(statisticsService));
      return courseDefUpdater;
   }

   @DependsOn({COURSE_DEF_UPDATER_BEAN, USER_CONFIG_REPOSITORY_BEAN})
   @Bean(name = COURSE_DEF_UPDATER_SERVICE_BEAN)
   public CourseDefUpdaterService getCourseDefUpdaterService(@Autowired CourseDefUpdater courseDefUpdater,
                                                             @Autowired UserConfigRepository userConfigRepository) {
      return new CourseDefUpdaterService(courseDefUpdater, new UserConfigService(userConfigRepository));
   }
}

