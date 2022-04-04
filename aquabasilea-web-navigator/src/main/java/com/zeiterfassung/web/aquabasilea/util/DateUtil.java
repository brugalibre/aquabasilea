package com.zeiterfassung.web.aquabasilea.util;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;

public class DateUtil {

   public static final String DATE_TIME_FORMAT_DD_MM_YYY_HH_MM = "dd.MM.yyyy, HH:mm";

   private DateUtil() {
      // private
   }

   /**
    * Returns a {@link DayOfWeek} for the given description of a day-of-the week in the given language!
    *
    * @param dayOfTheWeekAsString the day of the week as german word
    * @return a {@link DayOfWeek} for the given description of a day-of-the week in the given language
    */
   public static DayOfWeek getDayOfWeekFromInput(String dayOfTheWeekAsString, Locale locale) {
      for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
         if (dayOfWeek.getDisplayName(TextStyle.FULL, locale).equals(dayOfTheWeekAsString)) {
            return dayOfWeek;
         }
      }
      return null;
   }
}
