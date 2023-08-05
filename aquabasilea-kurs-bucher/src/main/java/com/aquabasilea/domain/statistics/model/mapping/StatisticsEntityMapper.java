package com.aquabasilea.domain.statistics.model.mapping;

import com.aquabasilea.domain.statistics.model.Statistics;
import com.aquabasilea.persistence.statistics.StatisticsEntity;
import com.brugalibre.common.domain.mapper.CommonDomainModelMapper;
import org.mapstruct.Mapper;

@Mapper
public interface StatisticsEntityMapper extends CommonDomainModelMapper<Statistics, StatisticsEntity> {
   // no-op
}