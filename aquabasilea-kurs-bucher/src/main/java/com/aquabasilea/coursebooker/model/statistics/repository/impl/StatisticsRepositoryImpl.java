package com.aquabasilea.coursebooker.model.statistics.repository.impl;

import com.aquabasilea.coursebooker.model.statistics.Statistics;
import com.aquabasilea.coursebooker.model.statistics.mapping.StatisticsEntityMapperImpl;
import com.aquabasilea.coursebooker.model.statistics.repository.StatisticsRepository;
import com.aquabasilea.coursebooker.persistence.statistic.StatisticsEntity;
import com.aquabasilea.coursebooker.persistence.statistic.dao.StatisticsDao;
import com.brugalibre.common.domain.repository.CommonDomainRepositoryImpl;

public class StatisticsRepositoryImpl extends CommonDomainRepositoryImpl<Statistics, StatisticsEntity, StatisticsDao> implements StatisticsRepository {
   public StatisticsRepositoryImpl(StatisticsDao statisticsDao) {
      super(statisticsDao, new StatisticsEntityMapperImpl());
   }

   @Override
   public Statistics getByUserId(String userId) {
      return domainModelMapper.map2DomainModel(domainDao.getByUserId(userId));
   }
}
