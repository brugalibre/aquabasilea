package com.aquabasilea.persistence.entity.statistic;

import com.aquabasilea.course.aquabasilea.CourseDef;
import com.aquabasilea.statistics.model.Statistics;
import com.aquabasilea.statistics.repository.StatisticsRepository;

import java.time.LocalDateTime;

public class StatisticsHelper {

   private final StatisticsRepository statisticsRepository;

   public StatisticsHelper(StatisticsRepository statisticsRepository) {
      this.statisticsRepository = statisticsRepository;
   }

   /**
    * Sets the {@link Statistics#getLastCourseDefUpdate()} attribut and persist the changes
    *
    * @param lastCourseDefUpdate the new value of the lastCourseDefUpdate
    */
   public void setLastCourseDefUpdate(LocalDateTime lastCourseDefUpdate) {
      Statistics statistics = getStatisticsDto();
      statistics.setLastCourseDefUpdate(lastCourseDefUpdate);
      statisticsRepository.saveOrUpdate(statistics);
   }


   /**
    * Sets the {@link LocalDateTime} when the {@link CourseDef} are updated the next time
    *
    * @param nextCourseDefUpdate the {@link LocalDateTime} when the {@link CourseDef} are updated the next time
    */
   public void setNextCourseDefUpdate(LocalDateTime nextCourseDefUpdate) {
      Statistics statistics = getStatisticsDto();
      statistics.setNextCourseDefUpdate(nextCourseDefUpdate);
      statisticsRepository.saveOrUpdate(statistics);
   }

   public Statistics getStatisticsDto() {
      return statisticsRepository.findFirstStatisticsDto();
   }

   public boolean needsCourseDefUpdate() {
      return getStatisticsDto().needsCourseDefUpdate();
   }
}
