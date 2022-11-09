package com.aquabasilea.model.statistics.mapping;

import com.aquabasilea.persistence.entity.statistic.StatisticsEntity;
import com.aquabasilea.model.statistics.Statistics;
import com.brugalibre.common.domain.mapper.CommonDomainModelMapper;
import org.mapstruct.Mapper;

@Mapper
public interface StatisticsEntityMapper extends CommonDomainModelMapper<Statistics, StatisticsEntity> {
   // no-op
}