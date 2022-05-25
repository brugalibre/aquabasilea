package com.aquabasilea.rest.model.statistic;

import com.aquabasilea.statistics.model.Statistics;
import com.aquabasilea.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.Locale;

import static java.util.Objects.isNull;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record StatisticsDto(String lastCourseDefUpdate, String nextCourseDefUpdate, String uptimeRepresentation) {
   public static StatisticsDto of(Statistics statistics, Locale locale) {
      return new StatisticsDto(getLocaleDateTime2String(statistics.getLastCourseDefUpdate(), locale), getLocaleDateTime2String(statistics.getNextCourseDefUpdate(), locale), "");
   }

   @NotNull
   private static String getLocaleDateTime2String(LocalDateTime localDateTime, Locale locale) {
      if (isNull(localDateTime)) {
         return " - ";
      }
      return DateUtil.toString(localDateTime, locale);
   }
}
