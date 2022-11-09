package com.aquabasilea.rest.model.statistic;

import com.aquabasilea.i18n.TextResources;
import com.aquabasilea.model.statistics.Statistics;
import com.aquabasilea.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Locale;

import static java.util.Objects.isNull;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record StatisticsDto(String lastCourseDefUpdate, String nextCourseDefUpdate, int totalBookingCounter,
                            double bookingSuccessRate, String uptimeRepresentation) {
   public static StatisticsDto of(Statistics statistics, Locale locale, String uptimeRep) {
      int totalBookingCounter = statistics.getBookingSuccessfulCounter() + statistics.getBookingFailedCounter();
      double bookingSuccessRate = getBookingSuccessRate(totalBookingCounter, statistics.getBookingSuccessfulCounter());
      return new StatisticsDto(getLocaleDateTime2String(statistics.getLastCourseDefUpdate(), locale),
              getLocaleDateTime2String(statistics.getNextCourseDefUpdate(), locale),
              totalBookingCounter, bookingSuccessRate,
              uptimeRep);
   }

   private static double getBookingSuccessRate(int totalBookingCounter, int bookingSuccessfulCounter) {
      if (totalBookingCounter == 0) {
         return 0;
      }
      BigDecimal bookingSuccessfulCounterBD = BigDecimal.valueOf(bookingSuccessfulCounter);
      BigDecimal totalBookingCounterBD = BigDecimal.valueOf(totalBookingCounter);
      return bookingSuccessfulCounterBD.divide(totalBookingCounterBD, 3, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue();
   }

   private static String getLocaleDateTime2String(LocalDateTime localDateTime, Locale locale) {
      if (isNull(localDateTime)) {
         return " - ";
      }
      return TextResources.NEXT_LAST_AQUABASILEA_COURSE_DEF_UPDATE.formatted(DateUtil.toString(localDateTime, locale));
   }
}
