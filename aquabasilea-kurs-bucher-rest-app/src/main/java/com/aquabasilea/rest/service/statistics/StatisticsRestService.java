package com.aquabasilea.rest.service.statistics;

import com.aquabasilea.application.i18n.TextResources;
import com.aquabasilea.rest.i18n.LocaleProvider;
import com.aquabasilea.rest.model.statistic.StatisticsDto;
import com.aquabasilea.service.statistics.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;

@Service
public class StatisticsRestService {

   private final StatisticsService statisticsService;
   private final LocaleProvider localeProvider;
   private final RuntimeMXBean runtimeMXBean;

   @Autowired
   public StatisticsRestService(StatisticsService statisticsService, LocaleProvider localeProvider) {
      this(statisticsService, localeProvider, ManagementFactory.getRuntimeMXBean());
   }

   StatisticsRestService(StatisticsService statisticsService, LocaleProvider localeProvider, RuntimeMXBean runtimeMXBean) {
      this.localeProvider = localeProvider;
      this.statisticsService = statisticsService;
      this.runtimeMXBean = runtimeMXBean;
   }

   public StatisticsDto getStatisticDtoByUserId(String userId) {
      return StatisticsDto.of(statisticsService.getStatisticsOverviewByUserId(userId), localeProvider.getCurrentLocale(), getDurationString());
   }

   public String getDurationString() {
      return getDurationRelative(LocalDateTime.now());
   }

   String getDurationRelative(LocalDateTime now) {
      Duration uptimeDuration = getUptimeDuration();
      LocalDateTime startTime = now.minusNanos(uptimeDuration.toNanos());
      Period between = Period.between(startTime.toLocalDate(), now.toLocalDate());
      long years = between.getYears();
      long days = between.getDays();
      long month = between.getMonths();
      String yearsAndMonth = "";
      if (years > 0 || month > 0) {
         yearsAndMonth = TextResources.UPTIME_YEARS_AND_MONTH.formatted(years, month) + ", ";
      }
      return yearsAndMonth + TextResources.UPTIME_DAYS_HOURS_MINUTES.formatted(days, uptimeDuration.toHoursPart(), uptimeDuration.toMinutesPart());
   }

   public Duration getUptimeDuration() {
      return Duration.ofMillis(runtimeMXBean.getUptime());
   }
}
