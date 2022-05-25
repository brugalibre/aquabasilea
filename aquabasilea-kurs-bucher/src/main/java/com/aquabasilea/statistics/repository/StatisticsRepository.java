package com.aquabasilea.statistics.repository;

import com.aquabasilea.persistence.entity.statistic.StatisticsEntity;
import com.aquabasilea.statistics.model.Statistics;

/**
 * The {@link StatisticsRepository} is responsible for loading and saving {@link Statistics}s
 */
public interface StatisticsRepository {
   /**
    * Finds the first persistent {@link Statistics}s
    *
    * @return the first persistent {@link Statistics}s
    */
   Statistics findFirstStatisticsDto();

   /**
    * Saves the given {@link Statistics}s
    *
    * @param statistics the given {@link Statistics}s to persist or update
    * @return the persisted {@link Statistics}s
    */
   Statistics saveOrUpdate(Statistics statistics);

   /**
    * Deletes all {@link StatisticsEntity}
    */
   void deleteAll();
}
