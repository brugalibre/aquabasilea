package com.aquabasilea.model.statistics.repository.impl;

import com.aquabasilea.model.statistics.mapping.StatisticsEntityMapperImpl;
import com.aquabasilea.persistence.entity.statistic.StatisticsEntity;
import com.aquabasilea.persistence.entity.statistic.dao.StatisticsDao;
import com.aquabasilea.model.statistics.Statistics;
import com.aquabasilea.model.statistics.repository.StatisticsRepository;
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
