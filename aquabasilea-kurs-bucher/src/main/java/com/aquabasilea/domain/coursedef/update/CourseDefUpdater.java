package com.aquabasilea.domain.coursedef.update;

import com.aquabasilea.domain.courselocation.model.CourseLocation;
import com.aquabasilea.domain.coursebooker.states.booking.facade.CourseDefExtractorFacade;
import com.aquabasilea.domain.coursedef.model.CourseDef;
import com.aquabasilea.domain.coursedef.model.repository.CourseDefRepository;
import com.aquabasilea.domain.coursedef.update.notify.CourseDefUpdatedNotifier;
import com.aquabasilea.domain.coursedef.update.notify.CourseDefUpdaterStartedNotifier;
import com.aquabasilea.domain.coursedef.update.notify.OnCourseDefsUpdatedContext;
import com.aquabasilea.domain.coursedef.update.notify.OnSchedulerStartContext;
import com.aquabasilea.domain.userconfig.model.UserConfig;
import com.aquabasilea.domain.userconfig.repository.UserConfigRepository;
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
import java.util.function.Function;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

public class CourseDefUpdater {

   private static final Logger LOG = LoggerFactory.getLogger(CourseDefUpdater.class);

   // Repositories
   private final CourseDefRepository courseDefRepository;
   private final UserConfigRepository userConfigRepository;
   private final Function<String, Boolean> courseDefUpdateNecessary4User;

   private final CourseDefExtractorFacade courseBookerFacade;
   private final CourseDefUpdaterScheduler courseDefUpdaterScheduler;

   // Notifiers
   private final List<CourseDefUpdatedNotifier> courseDefUpdatedNotifiers;
   private final List<CourseDefUpdaterStartedNotifier> courseDefUpdaterStartedNotifiers;
   private final Map<String, Boolean> userId2IsCourseDefUpdateRunningMap;

   public CourseDefUpdater(CourseDefExtractorFacade courseBookerFacade, Function<String, Boolean> courseDefUpdateNecessary4User,
                           CourseDefRepository courseDefRepository, UserConfigRepository userConfigRepository) {
      this(courseBookerFacade, courseDefUpdateNecessary4User, courseDefRepository, userConfigRepository, new CourseDefUpdateDate());
   }

   public CourseDefUpdater(CourseDefExtractorFacade courseBookerFacade, Function<String, Boolean> courseDefUpdateNecessary4User,
                           CourseDefRepository courseDefRepository, UserConfigRepository userConfigRepository, CourseDefUpdateDate courseDefUpdateDate) {
      this.courseBookerFacade = courseBookerFacade;
      this.userConfigRepository = userConfigRepository;
      this.courseDefRepository = courseDefRepository;
      this.courseDefUpdateNecessary4User = courseDefUpdateNecessary4User;
      this.courseDefUpdaterScheduler = new CourseDefUpdaterScheduler(this::updateCourseDefsAsRunnable, courseDefUpdateDate);
      this.courseDefUpdatedNotifiers = new ArrayList<>();
      this.courseDefUpdaterStartedNotifiers = new ArrayList<>();
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
      OnSchedulerStartContext onSchedulerStartContext = new OnSchedulerStartContext(userId, nextCourseDefUpdate);
      courseDefUpdaterStartedNotifiers.forEach(courseDefStartedNotifier -> courseDefStartedNotifier.onSchedulerStarted(onSchedulerStartContext));
      if (courseDefUpdateNecessary4User.apply(userId)) {
         this.updateCourseDefs(userId);
         LOG.info("CourseDefUpdater scheduling: Course-defs updated");
      }
      LOG.info("CourseDefUpdater scheduling done");
   }

   /**
    * Updates all {@link CourseDef} according user's configuration (which defines the {@link CourseLocation}s to consider)
    * According to those the aquabasliea-courses defined on their course-page are considered
    * <p>
    *<b>Note:</b> This call is blocking until the {@link CourseDef}s are upated
    *
    * @param userId the id of the {@link User}
    */
   public void updateCourseDefs(String userId) {
      if (isCourseDefUpdateRunning(userId)) {
         LOG.warn("CourseDefs are already being updated, do nothing!");
         return;
      }
      updateCourseDefsAsRunnable(userId);
   }

   private void updateCourseDefsAsRunnable(String userId) {
      try {
         UserConfig userConfig = userConfigRepository.getByUserId(userId);
         userId2IsCourseDefUpdateRunningMap.put(userId, true);
         LOG.info("Updating course-defs..");
         LocalDateTime start = LocalDateTime.now();
         updateCourseDefsInternal(userId, userConfig.getCourseLocations(), start);
         Duration duration = Duration.ofMillis(start.until(LocalDateTime.now(), ChronoUnit.MILLIS));
         LOG.info("Updating course-defs done, duration: {}", duration);
      } catch (Exception e) {
         LOG.error("Error while executing the CourseDefUpdater!", e);
      } finally {
         userId2IsCourseDefUpdateRunningMap.put(userId, false);
      }
   }

   private void updateCourseDefsInternal(String userId, List<CourseLocation> courseLocations, LocalDateTime dateWhenUpdateStarted) {
      List<CourseDef> courseDefs = courseBookerFacade.getCourseDefs(userId, courseLocations);
      courseDefRepository.deleteAllByUserId(userId);
      courseDefRepository.saveAll(courseDefs);
      OnCourseDefsUpdatedContext onCourseDefsUpdatedContext = new OnCourseDefsUpdatedContext(userId, courseDefs, dateWhenUpdateStarted, courseDefUpdaterScheduler.calcDelayUntilNextUpdate());
      courseDefUpdatedNotifiers.forEach(courseDefUpdatedNotifier -> courseDefUpdatedNotifier.courseDefsUpdated(onCourseDefsUpdatedContext));
   }

   /**
    * @param userId the id of the {@link User}
    * @return <code>true</code> if there is currently an update running for the given user id. Returns <code>false</code> otherwise
    */
   public synchronized boolean isCourseDefUpdateRunning(String userId) {
      Boolean isCourseDefUpdateRunning = userId2IsCourseDefUpdateRunningMap.get(userId);
      return nonNull(isCourseDefUpdateRunning) && isCourseDefUpdateRunning;
   }

   /**
    * @return the duration from now until the next scheduled update or <code>null</code> if none is scheduled
    */
   public Duration calcDelayUntilNextUpdate() {
      return courseDefUpdaterScheduler.calcDelayUntilNextUpdate();
   }

   public void addCourseDefUpdatedNotifier(CourseDefUpdatedNotifier courseDefUpdatedNotifier) {
      courseDefUpdatedNotifiers.add(requireNonNull(courseDefUpdatedNotifier));
   }

   public void addCourseDefStartedNotifier(CourseDefUpdaterStartedNotifier courseDefUpdaterStartedNotifier) {
      courseDefUpdaterStartedNotifiers.add(requireNonNull(courseDefUpdaterStartedNotifier));
   }
}
