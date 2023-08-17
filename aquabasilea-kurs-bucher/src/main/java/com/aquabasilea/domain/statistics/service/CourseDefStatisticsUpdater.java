package com.aquabasilea.domain.statistics.service;

import com.aquabasilea.domain.coursedef.update.CourseDefUpdaterScheduler;
import com.aquabasilea.domain.coursedef.update.notify.CourseDefUpdatedNotifier;
import com.aquabasilea.domain.coursedef.update.notify.CourseDefUpdaterStartedNotifier;
import com.aquabasilea.domain.coursedef.update.notify.OnCourseDefsUpdatedContext;
import com.aquabasilea.domain.coursedef.update.notify.OnSchedulerStartContext;
import com.aquabasilea.domain.statistics.model.Statistics;
import com.aquabasilea.service.statistics.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * The {@link CourseDefStatisticsUpdater} updates the {@link Statistics} after a booking was completed
 */
public class CourseDefStatisticsUpdater implements CourseDefUpdatedNotifier, CourseDefUpdaterStartedNotifier {
   private static final Logger LOG = LoggerFactory.getLogger(CourseDefStatisticsUpdater.class);
   private final StatisticsService statisticsService;

   public CourseDefStatisticsUpdater(StatisticsService statisticsService) {
      this.statisticsService = statisticsService;
   }

   @Override
   public void onSchedulerStarted(OnSchedulerStartContext onSchedulerStartContext) {
      statisticsService.setNextCourseDefUpdate(onSchedulerStartContext.userId(), onSchedulerStartContext.nextCourseDefUpdate());
   }

   @Override
   public void courseDefsUpdated(OnCourseDefsUpdatedContext onCourseDefsUpdatedContext) {
   updateStatistics(onCourseDefsUpdatedContext.userId(), onCourseDefsUpdatedContext.dateWhenUpdateStarted(),
           onCourseDefsUpdatedContext.durationUntilNextUpdate());
   }

   private void updateStatistics(String userId, LocalDateTime dateWhenUpdateStarted, Duration durationUntilNextUpdate) {
            LocalDateTime nextCourseDefUpdate;
      // If there are exactly 24h between a schedule, then 'scheduledFuture.getDelay' returns 0s -> calculate the next iteration by adding the update-cycle
      // No idea why that's like this... but it's true
      if (durationUntilNextUpdate.toMinutes() == 0) {
         nextCourseDefUpdate = dateWhenUpdateStarted.plusNanos(CourseDefUpdaterScheduler.getCourseDefUpdateCycle().toNanos());
      } else {
         nextCourseDefUpdate = dateWhenUpdateStarted.plusNanos(durationUntilNextUpdate.toNanos());
      }
      LOG.info("Updating statistics: Updated started at '{}', next update is at '{}'", dateWhenUpdateStarted, nextCourseDefUpdate);
      statisticsService.setLastCourseDefUpdate(userId, dateWhenUpdateStarted);
      statisticsService.setNextCourseDefUpdate(userId, nextCourseDefUpdate);
   }
}
