package com.aquabasilea.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;

public class DateUtil {

   private DateUtil() {
      // private
   }

   private static final String DATE_FORMAT_DD_MM_YYY = "dd.MM.yyyy";
   public static final String DATE_TIME_FORMAT_DD_MM_YYY_HH_MM = "dd.MM.yyyy, HH:mm";
   public static final String DATE_TIME_FORMAT_DD_MM_YYY_HH_MM_SS = "dd.MM.yyyy, HH:mm:ss";

   /**
    * Returns the amount of milliseconds for the given {@link LocalDateTime}
    *
    * @param date the {@link LocalDateTime} to calc the amount of milliseconds for
    * @return the amount of milliseconds for the given {@link LocalDateTime}
    */
   public static long getMillis(LocalDateTime date) {
      return date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
   }

   /**
    * Creates a {@link LocalDateTime} from the given string input. This value must follow the pattern {@link DateUtil#DATE_TIME_FORMAT_DD_MM_YYY_HH_MM}
    *
    * @param dateAsString the String value
    * @param locale       the {@link Locale}
    * @return a {@link LocalDateTime} instance
    */
   public static LocalDateTime getLocalDateTimeFromString(String dateAsString, Locale locale) {
      return LocalDateTime.parse(dateAsString, DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_DD_MM_YYY_HH_MM, locale));
   }

   /**
    * Returns the maximum possible day of the month, for the given {@link LocalDate}
    *
    * @param localDate the {@link LocalDate} to check
    * @return the maximum possible day of the month
    */
   public static int getLastDayOfMonth(LocalDateTime localDate) {
      return localDate.with(TemporalAdjusters.lastDayOfMonth()).get(ChronoField.DAY_OF_MONTH);
   }

   /**
    * Returns a String representation of the given {@link LocalDateTime} in the given {@link Locale}
    * The date representation is in the following pattern: DATE_TIME_FORMAT_DD_MM_YYY_HH_MM
    *
    * @param courseDate the date-time
    * @param locale     the local
    * @return a String representation of the given {@link LocalDateTime} in the given {@link Locale}
    */
   public static String toString(LocalDateTime courseDate, Locale locale) {
      return courseDate.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_DD_MM_YYY_HH_MM, locale));
   }

   /**
    * Returns a String representation of the given {@link LocalDate} in the given {@link Locale}
    * The date representation is in the following pattern: DATE_TIME_FORMAT_DD_MM_YYY
    *
    * @param date the date
    * @param locale     the local
    * @return a String representation of the given {@link LocalDate} in the given {@link Locale}
    */
   public static String toString(LocalDate date, Locale locale) {
      return date.format(DateTimeFormatter.ofPattern(DATE_FORMAT_DD_MM_YYY, locale));
   }

   /**
    * Returns a String representation of the given {@link LocalDateTime} in the given {@link Locale}
    * The date representation is in the following pattern: DATE_TIME_FORMAT_DD_MM_YYY_HH_MM_SS
    *
    * @param courseDate the date-time
    * @param locale     the local
    * @return a String representation of the given {@link LocalDateTime} in the given {@link Locale}
    */
   public static String toStringWithSeconds(LocalDateTime courseDate, Locale locale) {
      return courseDate.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_DD_MM_YYY_HH_MM_SS, locale));
   }

   /**
    * Calculates the amount of milliseconds from now until the time the given {@link LocalDateTime}.
    *
    * @param date the {@link LocalDateTime} when the course takes place
    * @return the amount of milliseconds from now until the time the given {@link LocalDateTime}
    */
   public static long calcTimeLeftBeforeDate(LocalDateTime date) {
      LocalDateTime now = LocalDateTime.now();
      LocalDateTime bookCourseAt = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), date.getHour(), date.getMinute());
      return DateUtil.getMillis(bookCourseAt) - DateUtil.getMillis(now);
   }

   public static String getTimeAsString(LocalDateTime localDateTime) {
      return localDateTime.getHour() + ":" + (localDateTime.getMinute() >= 10 ? localDateTime.getMinute() : "0" + localDateTime.getMinute());
   }
}
