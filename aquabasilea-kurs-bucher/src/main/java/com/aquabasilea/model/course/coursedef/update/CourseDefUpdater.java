package com.aquabasilea.model.course.coursedef.update;

import com.aquabasilea.model.course.coursedef.update.notify.CourseDefUpdatedNotifier;
import com.aquabasilea.model.course.CourseLocation;
import com.aquabasilea.model.course.coursedef.CourseDef;
import com.aquabasilea.model.course.coursedef.repository.CourseDefRepository;
import com.aquabasilea.model.course.coursedef.repository.mapping.CoursesDefEntityMapper;
import com.aquabasilea.model.course.coursedef.repository.mapping.CoursesDefEntityMapperImpl;
import com.aquabasilea.model.userconfig.UserConfig;
import com.aquabasilea.model.userconfig.repository.UserConfigRepository;
import com.aquabasilea.service.statistics.StatisticsService;
import com.aquabasilea.web.extractcourses.AquabasileaCourseExtractor;
import com.aquabasilea.web.extractcourses.model.ExtractedAquabasileaCourses;
import com.brugalibre.domain.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

public class CourseDefUpdater {

   private static final Logger LOG = LoggerFactory.getLogger(CourseDefUpdater.class);

   // Repositories
   private final CourseDefRepository courseDefRepository;
   private final CoursesDefEntityMapper coursesDefEntityMapper;
   private final UserConfigRepository userConfigRepository;
   private final StatisticsService statisticsService;

   private final Supplier<AquabasileaCourseExtractor> aquabasileaCourseExtractorSupplier;
   private final ExecutorService executorService;
   private final CourseDefUpdaterScheduler courseDefUpdaterScheduler;
   private final List<CourseDefUpdatedNotifier> courseDefUpdatedNotifiers;
   private final Map<String, Boolean> userId2IsCourseDefUpdateRunningMap;

   public CourseDefUpdater(Supplier<AquabasileaCourseExtractor> aquabasileaCourseExtractorSupplier, StatisticsService statisticsService, CourseDefRepository courseDefRepository,
                           UserConfigRepository userConfigRepository) {
      this(aquabasileaCourseExtractorSupplier, statisticsService, courseDefRepository, userConfigRepository, new CourseDefUpdateDate());
   }

   public CourseDefUpdater(Supplier<AquabasileaCourseExtractor> aquabasileaCourseExtractorSupplier, StatisticsService statisticsService,
                           CourseDefRepository courseDefRepository, UserConfigRepository userConfigRepository, CourseDefUpdateDate courseDefUpdateDate) {
      this.aquabasileaCourseExtractorSupplier = aquabasileaCourseExtractorSupplier;
      this.coursesDefEntityMapper = new CoursesDefEntityMapperImpl();
      this.userConfigRepository = userConfigRepository;
      this.courseDefRepository = courseDefRepository;
      this.statisticsService = statisticsService;
      this.executorService = Executors.newSingleThreadExecutor();
      this.courseDefUpdaterScheduler = new CourseDefUpdaterScheduler(this::updateCourseDefsAsRunnable, courseDefUpdateDate);
      this.courseDefUpdatedNotifiers = new ArrayList<>();
      this.userId2IsCourseDefUpdateRunningMap = new HashMap<>();
   }

   /**
    * Prepares and starts the scheduler which then does automatically update the {@link CourseDef} in a well-defined
    * period
    * If there was never an update or the update is too old, then this method starts an update immediately!
    *
    * @param userId the id of the {@link User}
    */
   public void startScheduler(String userId) {
      LocalDateTime nextCourseDefUpdate = this.courseDefUpdaterScheduler.startScheduler(userId);
      statisticsService.setNextCourseDefUpdate(userId, nextCourseDefUpdate);
      if (statisticsService.needsCourseDefUpdate(userId)) {
         this.updateAquabasileaCourses(userId);
      }
   }

   /**
    * Updates all {@link CourseDef} according user's configuration (which defines the {@link CourseLocation}s to consider)
    * According to those the aquabasliea-courses defined on their course-page are considered
    *
    * @param userId the id of the {@link User}
    */
   public void updateAquabasileaCourses(String userId) {
      if (isCourseDefUpdateRunning(userId)) {
         LOG.warn("CourseDefs are already being updated, do nothing!");
         return;
      }
      this.executorService.submit(() -> updateCourseDefsAsRunnable(userId));
   }

   private void updateCourseDefsAsRunnable(String userId) {
      try {
         UserConfig userConfig = userConfigRepository.getByUserId(userId);
         userId2IsCourseDefUpdateRunningMap.put(userId, true);
         LOG.info("Updating course-defs..");
         LocalDateTime start = LocalDateTime.now();
         updateAquabasileaCoursesInternal(userId, userConfig.getCourseLocations());
         Duration duration = Duration.ofMillis(start.until(LocalDateTime.now(), ChronoUnit.MILLIS));
         LOG.info("Updating course-defs done, duration: {}", duration);
         updateStatistics(userId, start, duration);
      } catch (Exception e) {
         LOG.error("Error while executing the CourseDefUpdater!", e);
      } finally {
         userId2IsCourseDefUpdateRunningMap.put(userId, false);
      }
   }

   private void updateStatistics(String userId, LocalDateTime dateWhenUpdateStarted, Duration lastUpdateDuration) {
      Duration durationUntilNextUpdate = courseDefUpdaterScheduler.calcDelayUntilNextUpdate()
              .minus(lastUpdateDuration);
      statisticsService.setLastCourseDefUpdate(userId, dateWhenUpdateStarted);
      statisticsService.setNextCourseDefUpdate(userId, dateWhenUpdateStarted.plusNanos(durationUntilNextUpdate.toNanos()));
   }

   private void updateAquabasileaCoursesInternal(String userId, List<CourseLocation> courseLocations) {
      List<com.aquabasilea.web.model.CourseLocation> webCourseLocations = map2WebCourseLocation(courseLocations);
      ExtractedAquabasileaCourses extractedAquabasileaCourses = aquabasileaCourseExtractorSupplier.get().extractAquabasileaCourses(webCourseLocations);
      courseDefRepository.deleteAllByUserId(userId);
      List<CourseDef> courseDefs = map2CourseDefsAndSetUserId(userId, extractedAquabasileaCourses);
      courseDefRepository.saveAll(courseDefs);
      courseDefUpdatedNotifiers.forEach(courseDefUpdatedNotifier -> courseDefUpdatedNotifier.courseDefsUpdated(userId, courseDefs));
   }

   private List<CourseDef> map2CourseDefsAndSetUserId(String userId, ExtractedAquabasileaCourses extractedAquabasileaCourses) {
      return coursesDefEntityMapper.mapAquabasileaCourses2CourseDefs(extractedAquabasileaCourses.getAquabasileaCourses())
              .stream()
              .map(courseDef -> courseDef.setUserId(userId))
              .toList();
   }

   /**
    * @param userId the id of the {@link User}
    * @return <code>true</code> if there is currently an update running for the given user id. Returns <code>false</code> otherwise
    */
   public synchronized boolean isCourseDefUpdateRunning(String userId) {
      Boolean isCourseDefUpdateRunning = userId2IsCourseDefUpdateRunningMap.get(userId);
      return nonNull(isCourseDefUpdateRunning) && isCourseDefUpdateRunning;
   }

   public void addCourseDefUpdatedNotifier(CourseDefUpdatedNotifier courseDefUpdatedNotifier) {
      courseDefUpdatedNotifiers.add(requireNonNull(courseDefUpdatedNotifier));
   }

   private static List<com.aquabasilea.web.model.CourseLocation> map2WebCourseLocation(List<CourseLocation> courseLocations) {
      return courseLocations.stream()
              .map(CourseLocation::getWebCourseLocation)
              .collect(Collectors.toList());
   }
}
