package com.aquabasilea.coursedef.update;

import com.aquabasilea.coursebooker.model.course.CourseLocation;
import com.aquabasilea.coursebooker.model.userconfig.UserConfig;
import com.aquabasilea.coursebooker.model.userconfig.repository.UserConfigRepository;
import com.aquabasilea.coursebooker.service.statistics.StatisticsService;
import com.aquabasilea.coursedef.model.CourseDef;
import com.aquabasilea.coursedef.model.repository.CourseDefRepository;
import com.aquabasilea.coursedef.update.facade.CourseExtractorFacade;
import com.aquabasilea.coursedef.update.notify.CourseDefUpdatedNotifier;
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

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

public class CourseDefUpdater {

   private static final Logger LOG = LoggerFactory.getLogger(CourseDefUpdater.class);

   // Repositories
   private final CourseDefRepository courseDefRepository;
   private final UserConfigRepository userConfigRepository;
   private final StatisticsService statisticsService;

   private final CourseExtractorFacade courseExtractorFacade;
   private final ExecutorService executorService;
   private final CourseDefUpdaterScheduler courseDefUpdaterScheduler;
   private final List<CourseDefUpdatedNotifier> courseDefUpdatedNotifiers;
   private final Map<String, Boolean> userId2IsCourseDefUpdateRunningMap;

   public CourseDefUpdater(CourseExtractorFacade courseExtractorFacade, StatisticsService statisticsService, CourseDefRepository courseDefRepository,
                           UserConfigRepository userConfigRepository) {
      this(courseExtractorFacade, statisticsService, courseDefRepository, userConfigRepository, new CourseDefUpdateDate());
   }

   public CourseDefUpdater(CourseExtractorFacade courseExtractorFacade, StatisticsService statisticsService,
                           CourseDefRepository courseDefRepository, UserConfigRepository userConfigRepository, CourseDefUpdateDate courseDefUpdateDate) {
      this.courseExtractorFacade = courseExtractorFacade;
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
         updateStatistics(userId, start);
      } catch (Exception e) {
         LOG.error("Error while executing the CourseDefUpdater!", e);
      } finally {
         userId2IsCourseDefUpdateRunningMap.put(userId, false);
      }
   }

   private void updateStatistics(String userId, LocalDateTime dateWhenUpdateStarted) {
      Duration durationUntilNextUpdate = courseDefUpdaterScheduler.calcDelayUntilNextUpdate();
      LocalDateTime nextCourseDefUpdate;
      // If there are exactly 24h between a schedule, then 'scheduledFuture.getDelay' returns 0s -> calculate the next iteration by adding the update-cycle
      // No idea why that's like this... but its true
      if (durationUntilNextUpdate.toMinutes() == 0) {
         nextCourseDefUpdate = dateWhenUpdateStarted.plusNanos(CourseDefUpdaterScheduler.getCourseDefUpdateCycle().toNanos());
      } else {
         nextCourseDefUpdate = dateWhenUpdateStarted.plusNanos(durationUntilNextUpdate.toNanos());
      }
      LOG.info("Updating statistics: Updated started at '{}', next update is at '{}'", dateWhenUpdateStarted, nextCourseDefUpdate);
      statisticsService.setLastCourseDefUpdate(userId, dateWhenUpdateStarted);
      statisticsService.setNextCourseDefUpdate(userId, nextCourseDefUpdate);
   }

   private void updateAquabasileaCoursesInternal(String userId, List<CourseLocation> courseLocations) {
      List<CourseDef> courseDefs = courseExtractorFacade.extractAquabasileaCourses(userId, courseLocations);
      courseDefRepository.deleteAllByUserId(userId);
      courseDefRepository.saveAll(courseDefs);
      courseDefUpdatedNotifiers.forEach(courseDefUpdatedNotifier -> courseDefUpdatedNotifier.courseDefsUpdated(userId, courseDefs));
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
}
