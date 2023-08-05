package com.aquabasilea.domain.statistics.model.repository.impl;

import com.aquabasilea.domain.statistics.model.Statistics;
import com.aquabasilea.domain.statistics.model.mapping.StatisticsEntityMapperImpl;
import com.aquabasilea.domain.statistics.model.repository.StatisticsRepository;
import com.aquabasilea.persistence.statistics.StatisticsEntity;
import com.aquabasilea.persistence.statistics.dao.StatisticsDao;
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
