package com.aquabasilea.statistics.mapping;

import com.aquabasilea.persistence.entity.statistic.StatisticsEntity;
import com.aquabasilea.statistics.model.Statistics;
import org.mapstruct.Mapper;

@Mapper
public interface StatisticsEntityMapper {

   StatisticsEntity map2StatisticEntity(Statistics statistics);

   Statistics map2StatisticsDto(StatisticsEntity statisticsEntity);
}