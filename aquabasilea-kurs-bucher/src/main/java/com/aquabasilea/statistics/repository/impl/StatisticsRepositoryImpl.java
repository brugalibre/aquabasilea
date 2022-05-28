package com.aquabasilea.statistics.repository.impl;

import com.aquabasilea.statistics.mapping.StatisticsEntityMapperImpl;
import com.aquabasilea.persistence.entity.statistic.StatisticsEntity;
import com.aquabasilea.persistence.entity.statistic.dao.StatisticsDao;
import com.aquabasilea.persistence.repository.SingleEntityRepositoryUtil;
import com.aquabasilea.statistics.mapping.StatisticsEntityMapper;
import com.aquabasilea.statistics.model.Statistics;
import com.aquabasilea.statistics.repository.StatisticsRepository;

import static java.util.Objects.isNull;

public class StatisticsRepositoryImpl implements StatisticsRepository {
   private final StatisticsDao statisticsDao;
   private final StatisticsEntityMapper statisticEntityMapper;

   public StatisticsRepositoryImpl(StatisticsDao statisticsDao) {
      this.statisticsDao = statisticsDao;
      this.statisticEntityMapper = new StatisticsEntityMapperImpl();
   }

   @Override
   public Statistics findFirstStatisticsDto() {
      StatisticsEntity statisticsEntity = SingleEntityRepositoryUtil.findFirstEntity(statisticsDao);
      if (isNull(statisticsEntity)) {
         Statistics statistics = new Statistics();
         return saveOrUpdate(statistics);
      }
      return statisticEntityMapper.map2StatisticsDto(statisticsEntity);
   }

   @Override
   public Statistics saveOrUpdate(Statistics statistics) {
      StatisticsEntity statisticsEntity = statisticEntityMapper.map2StatisticEntity(statistics);
      StatisticsEntity savedStatisticsEntity = statisticsDao.save(statisticsEntity);
      return statisticEntityMapper.map2StatisticsDto(savedStatisticsEntity);
   }

   @Override
   public void deleteAll() {
      statisticsDao.deleteAll();
   }
}
