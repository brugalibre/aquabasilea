package com.aquabasilea.coursebooker.model.statistics.mapping;

import com.aquabasilea.coursebooker.model.statistics.Statistics;
import com.aquabasilea.coursebooker.persistence.statistic.StatisticsEntity;
import com.brugalibre.common.domain.mapper.CommonDomainModelMapper;
import org.mapstruct.Mapper;

@Mapper
public interface StatisticsEntityMapper extends CommonDomainModelMapper<Statistics, StatisticsEntity> {
   // no-op
}