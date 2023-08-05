package com.aquabasilea.rest.model.statistic;

import com.aquabasilea.domain.statistics.model.Statistics;
import com.aquabasilea.domain.statistics.model.StatisticsOverview;
import com.aquabasilea.i18n.TextResources;
import com.aquabasilea.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.time.LocalDateTime;
import java.util.Locale;

import static java.util.Objects.isNull;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record StatisticsDto(String lastCourseDefUpdate, String nextCourseDefUpdate, int totalBookingCounter,
                            double bookingSuccessRate, String uptimeRepresentation) {
   public static StatisticsDto of(StatisticsOverview statisticsOverview, Locale locale, String uptimeRep) {
      Statistics statistics = statisticsOverview.statistics();
      return new StatisticsDto(getLocaleDateTime2String(statistics.getLastCourseDefUpdate(), locale),
              getLocaleDateTime2String(statistics.getNextCourseDefUpdate(), locale),
              statisticsOverview.totalBookingCounter(), statisticsOverview.bookingSuccessRate(),
              uptimeRep);
   }

   private static String getLocaleDateTime2String(LocalDateTime localDateTime, Locale locale) {
      if (isNull(localDateTime)) {
         return " - ";
      }
      return TextResources.NEXT_LAST_AQUABASILEA_COURSE_DEF_UPDATE.formatted(DateUtil.toString(localDateTime, locale));
   }
}
