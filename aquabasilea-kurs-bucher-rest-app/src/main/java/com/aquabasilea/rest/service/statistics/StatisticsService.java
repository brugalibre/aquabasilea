package com.aquabasilea.rest.service.statistics;

import com.aquabasilea.rest.i18n.LocalProvider;
import com.aquabasilea.rest.model.statistic.StatisticsDto;
import com.aquabasilea.statistics.repository.StatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService {

   private final StatisticsRepository statisticsRepository;
   private final LocalProvider localProvider;

   @Autowired
   public StatisticsService(StatisticsRepository statisticsRepository, LocalProvider localProvider) {
      this.statisticsRepository = statisticsRepository;
      this.localProvider = localProvider;
   }

   public StatisticsDto getStatisticDto() {
      return StatisticsDto.of(statisticsRepository.findFirstStatisticsDto(), localProvider.getCurrentLocale());
   }
}
