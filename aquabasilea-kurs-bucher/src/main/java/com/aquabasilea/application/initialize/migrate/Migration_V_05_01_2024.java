package com.aquabasilea.application.initialize.migrate;

import com.aquabasilea.application.config.logging.MdcConst;
import com.aquabasilea.application.initialize.api.AppInitializer;
import com.aquabasilea.application.initialize.common.InitType;
import com.aquabasilea.application.initialize.common.InitializeOrder;
import com.aquabasilea.domain.course.model.Course;
import com.aquabasilea.domain.course.model.Course.CourseBuilder;
import com.aquabasilea.domain.course.model.WeeklyCourses;
import com.aquabasilea.domain.course.repository.WeeklyCoursesRepository;
import com.aquabasilea.domain.coursebooker.states.booking.facade.CourseDefExtractorFacade;
import com.aquabasilea.domain.coursedef.model.CourseDef;
import com.aquabasilea.domain.coursedef.model.repository.CourseDefRepository;
import com.aquabasilea.domain.userconfig.model.UserConfig;
import com.aquabasilea.domain.userconfig.repository.UserConfigRepository;
import com.aquabasilea.service.statistics.StatisticsService;
import com.aquabasilea.util.version.VersionDetails;
import com.aquabasilea.util.version.VersionExtractor;
import com.brugalibre.domain.user.model.User;
import com.brugalibre.domain.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

import static com.aquabasilea.application.initialize.common.InitializationConst.COURSE_LOCATIONS;

/**
 * Specific one-time migrator for version 05.01.2024. Due to the moving from migros to ACTIV Fitness, the courselocations
 * are now also fetched and stored. That led to certain changes in the DB. According to the entries in the database
 * at the 05.01.2024, in this migration the new entries are created accordingly
 */

@Service
@InitializeOrder(order = COURSE_LOCATIONS + 10, type = {InitType.APP_STARTED})
public class Migration_V_05_01_2024 implements AppInitializer {
   private static final Logger LOG = LoggerFactory.getLogger(Migration_V_05_01_2024.USER_ID_BE_BO);

   private static final DefaultCourseInfos FUNCTIONAL_TRAINING = new DefaultCourseInfos("Functional Training 50' G1",
           LocalDateTime.of(2024, 1, 10, 18, 15), "16");
   private static final DefaultCourseInfos BE_BO = new DefaultCourseInfos("BeBo Fit 50' G1",
           LocalDateTime.of(2024, 1, 10, 8, 15), "16");
   private static final String USER_ID_BE_BO = "61b36398-7a04-4d97-84a6-5f3a713efd62";

   private static final String TARGET_VERSION = "2024.01.05";

   private final WeeklyCoursesRepository weeklyCoursesRepository;
   private final UserConfigRepository userConfigRepository;
   private final StatisticsService statisticsService;
   private final UserRepository userRepository;
   private final CourseDefRepository courseDefRepository;
   private final CourseDefExtractorFacade courseDefExtractorFacade;
   private final String prevVersion;

   public Migration_V_05_01_2024(WeeklyCoursesRepository weeklyCoursesRepository,
                                 UserConfigRepository userConfigRepository,
                                 StatisticsService statisticsService,
                                 CourseDefRepository courseDefRepository,
                                 UserRepository userRepository,
                                 CourseDefExtractorFacade courseDefExtractorFacade,
                                 @Value("${application.prevVersion}") String prevVersion) {
      this.weeklyCoursesRepository = weeklyCoursesRepository;
      this.userConfigRepository = userConfigRepository;
      this.courseDefRepository = courseDefRepository;
      this.statisticsService = statisticsService;
      this.userRepository = userRepository;
      this.prevVersion = prevVersion;
      this.courseDefExtractorFacade = courseDefExtractorFacade;
   }

   @Override
   public void initializeOnAppStart() {
      if (!isTargetVersionGreaterThanCurrent()) {
         LOG.info("Migration {} already migrated. Skip migration", this.getClass().getSimpleName());
         return;
      }
      LOG.info("Start migration from v{} to v{}", prevVersion, TARGET_VERSION);
      weeklyCoursesRepository.deleteAll();
      for (User user : userRepository.getAll()) {
         MDC.put(MdcConst.USER_ID, user.getId());
         String userId = user.id();
         migrateUserConfig(userId);
         migrateCourseDefs(userId);
         migrateWeeklyCourse(userId);
      }
   }

   private void migrateCourseDefs(String userId) {
      List<CourseDef> courseDefs = courseDefExtractorFacade.getCourseDefs(userId, userConfigRepository.getDefaultCourseLocations());
      courseDefRepository.saveAll(courseDefs);
      statisticsService.setLastCourseDefUpdate(userId, LocalDateTime.now());
   }

   private void migrateUserConfig(String userId) {
      userConfigRepository.save(new UserConfig(userId, userConfigRepository.getDefaultCourseLocations()));
   }

   private void migrateWeeklyCourse(String userId) {
      WeeklyCourses weeklyCourses = new WeeklyCourses(userId);
      courseDefRepository.getAllByUserId(userId)
              .stream()
              .filter(isDefaultCourseToAdd(userId))
              .map(this::mapToCourse)
              .forEach(weeklyCourses::addCourse);
      weeklyCoursesRepository.save(weeklyCourses);
   }

   private static Predicate<CourseDef> isDefaultCourseToAdd(String userId) {
      return courseDef -> getDefaultCoursesForUserId(userId).stream()
              .anyMatch(courseDefMatchesDefaultCourseToAdd(courseDef));
   }

   private static List<DefaultCourseInfos> getDefaultCoursesForUserId(String userId) {
      if (USER_ID_BE_BO.equals(userId)) {
         return List.of(FUNCTIONAL_TRAINING, BE_BO);
      }
      return List.of(FUNCTIONAL_TRAINING);
   }

   private static Predicate<DefaultCourseInfos> courseDefMatchesDefaultCourseToAdd(CourseDef courseDef) {
      return defaultCourseInfos ->
              defaultCourseInfos.courseName.equals(courseDef.courseName())
                      && defaultCourseInfos.centerId.equals(courseDef.courseLocation().centerId())
                      && defaultCourseInfos.courseDate.equals(courseDef.courseDate());
   }

   private Course mapToCourse(CourseDef courseDef) {
      LOG.info("Adding courseDef={}", courseDef);
      return CourseBuilder.builder()
              .withCourseLocation(courseDef.courseLocation())
              .withCourseDate(courseDef.courseDate())
              .withCourseInstructor(courseDef.courseInstructor())
              .withCourseName(courseDef.courseName())
              .withHasCourseDef(true)
              .build();
   }

   private boolean isTargetVersionGreaterThanCurrent() {
      VersionExtractor versionExtractor = new VersionExtractor();
      VersionDetails currentVersionDetails = versionExtractor.getVersionDetails(this.prevVersion);
      VersionDetails versionDetailsTarget = versionExtractor.getVersionDetails(TARGET_VERSION);
      return versionDetailsTarget.isGreater(currentVersionDetails);
   }

   private static class DefaultCourseInfos {
      String centerId;
      String courseName;
      LocalDateTime courseDate;

      DefaultCourseInfos(String courseName, LocalDateTime courseDate, String centerId) {
         this.courseName = courseName;
         this.courseDate = courseDate;
         this.centerId = centerId;
      }
   }
}
