package com.aquabasilea.course.aquabasilea.update;

import com.aquabasilea.course.CourseLocation;
import com.aquabasilea.course.aquabasilea.CourseDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * The {@link CourseDefUpdaterScheduler} schedules the regulary updates of the {@link CourseDef}s
 */
public class CourseDefUpdaterScheduler {
   private static final Logger LOG = LoggerFactory.getLogger(CourseDefUpdaterScheduler.class);
   private final ScheduledExecutorService scheduledExecutorService;
   private final List<CourseLocation> defaultCourseLocations;
   private final Consumer<List<CourseLocation>> courseUpdateConsumer;
   private final CourseDefUpdateDate courseDefUpdateDate;

   public CourseDefUpdaterScheduler(Consumer<List<CourseLocation>> courseUpdateConsumer, List<CourseLocation> defaultCourseLocations, CourseDefUpdateDate courseDefUpdateDate) {
      this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
      this.defaultCourseLocations = defaultCourseLocations;
      this.courseUpdateConsumer = courseUpdateConsumer;
      this.courseDefUpdateDate = courseDefUpdateDate;
   }

   /**
    * Starts an update immediately if necessary, or schedule one in the near future. It also schedules all future, regularly updates from then on
    *
    * @return the {@link LocalDateTime} when the next update takes place
    */
   public LocalDateTime startScheduler() {
      LocalDateTime courseDefUpdateDate = this.courseDefUpdateDate.calculateCourseDefUpdateLocalDateTime();
      Duration initDelay = calcInitialDelay(courseDefUpdateDate);
      Duration delayUntilTheNextUpdate = calcDelayUntilNextUpdate();
      LOG.info("Wait {} until first execution", initDelay);
      LOG.info("Wait {} after first execution until next", delayUntilTheNextUpdate);
      scheduledExecutorService.scheduleWithFixedDelay(() -> courseUpdateConsumer.accept(defaultCourseLocations), initDelay.toMinutes(),
              delayUntilTheNextUpdate.toMinutes(), TimeUnit.MINUTES);
      return LocalDateTime.now().plusMinutes(initDelay.toMinutes());
   }

   public Duration calcDelayUntilNextUpdate() {
      // Schedule every day -> one day à 24h à 60min each
      return Duration.ofMinutes(24 * 60);
   }

   private Duration calcInitialDelay(LocalDateTime courseDefUpdateDate) {
      long until = LocalDateTime.now().until(courseDefUpdateDate, ChronoUnit.MINUTES);
      return Duration.ofMinutes(until);
   }
}
