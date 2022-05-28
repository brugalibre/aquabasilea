package com.aquabasilea.rest.service.statistics;

import com.aquabasilea.i18n.TextResources;
import com.aquabasilea.rest.i18n.LocalProvider;
import com.aquabasilea.rest.model.statistic.StatisticsDto;
import com.aquabasilea.statistics.repository.StatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.Duration;

@Service
public class StatisticsService {

   private final StatisticsRepository statisticsRepository;
   private final RuntimeMXBean runtimeMXBean;
   private final LocalProvider localProvider;

   @Autowired
   public StatisticsService(StatisticsRepository statisticsRepository, LocalProvider localProvider) {
      this.statisticsRepository = statisticsRepository;
      this.localProvider = localProvider;
      this.runtimeMXBean = ManagementFactory.getRuntimeMXBean();
   }

   public StatisticsDto getStatisticDto() {
      return StatisticsDto.of(statisticsRepository.findFirstStatisticsDto(), localProvider.getCurrentLocale(), getDurationString());
   }

   private String getDurationString() {
      Duration uptimeDuration = Duration.ofMillis(runtimeMXBean.getUptime());
      long totalDays = uptimeDuration.toDays();
      long years = Math.floorDiv(totalDays, 365);
      long restDays = totalDays - (years * 365);
      long month = Math.floorDiv(restDays, 31);
      String yearsAndMonth = "";
      if (years > 0 || month > 0) {
         yearsAndMonth = TextResources.UPTIME_YEARS_AND_MONTH.formatted(years, month) + ", ";
      }
      return yearsAndMonth + TextResources.UPTIME_DAYS_HOURS_MINUTES.formatted(uptimeDuration.toDaysPart(), uptimeDuration.toHoursPart(), uptimeDuration.toMinutesPart());
   }
}
