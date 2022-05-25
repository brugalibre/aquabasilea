package com.aquabasilea.web.util;

import org.apache.commons.lang.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.Locale;

public class DateUtil {

   private static final String DAY_OF_THE_WEEK_SEPARATOR = " ";

   private DateUtil() {
      // private
   }

   /**
    * Returns a {@link LocalDate} for the given input of a date like 'Montag 13. Juni 2022'
    *
    * @param courseDateDescription a String representing a certain date
    * @param locale                defines the language in which the string representation of the date is written in
    * @return a {@link LocalDate} for the given input of a date like 'Montag 13. Juni 2022'
    */
   public static LocalDate getLocalDateFromInput(String courseDateDescription, Locale locale) {
      String dayOfWeekAsString = courseDateDescription.substring(0, courseDateDescription.indexOf(DAY_OF_THE_WEEK_SEPARATOR));

      // First get the day of the month
      String courseDateString2WorkWith = prepareCourseDate2WorkWithForNextValue(courseDateDescription, dayOfWeekAsString);
      String dayOfMonthAsString = courseDateString2WorkWith.substring(0, courseDateString2WorkWith.indexOf("."));
      courseDateString2WorkWith = prepareCourseDate2WorkWithForNextValue(courseDateString2WorkWith, dayOfMonthAsString);

      // Then get the month value
      String monthNameAsString = courseDateString2WorkWith.substring(courseDateString2WorkWith.indexOf(DAY_OF_THE_WEEK_SEPARATOR) + 1,
              courseDateString2WorkWith.lastIndexOf(DAY_OF_THE_WEEK_SEPARATOR));
      Month month = getMonth(locale, monthNameAsString);

      // And last but not least get the year
      courseDateString2WorkWith = prepareCourseDate2WorkWithForNextValue(courseDateString2WorkWith, monthNameAsString);
      String yearAsString = courseDateString2WorkWith.substring(courseDateString2WorkWith.length() - 4);// 4 because the year contains of 4 integers
      return LocalDate.of(Integer.parseInt(yearAsString), month, Integer.parseInt(dayOfMonthAsString));
   }

   /*
    * Use 'month.getDisplayName(TextStyle.FULL, locale).startsWith(monthNameAsString)' since it does not say 'August'
    * it just says 'Aug'
    */
   private static Month getMonth(Locale locale, String monthNameAsString) {
      return Arrays.stream(Month.values())
              .filter(month -> month.getDisplayName(TextStyle.FULL, locale).startsWith(monthNameAsString))
              .findFirst()
              .orElseThrow(() -> new IllegalStateException("There is no month for name '" + monthNameAsString + "'!"));
   }

   private static String prepareCourseDate2WorkWithForNextValue(String courseDateString2WorkWith, String currentValue2Replace) {
      return courseDateString2WorkWith.replaceFirst(currentValue2Replace, "").trim();
   }

   /**
    * Returns <code>true</code> if the given String starts with a day of the week and <code>false</code> if not
    *
    * @param value the value to test if it starts with a day of the week
    * @return <code>true</code> if the given String starts with a day of the week and <code>false</code> if not
    */
   public static boolean isStartsWithDayOfWeek(String value, Locale locale) {
      if (StringUtils.isEmpty(value)) {
         return false;
      }
      return Arrays.stream(DayOfWeek.values())
              .map(dayOfWeek -> dayOfWeek.getDisplayName(TextStyle.FULL, locale))
              .anyMatch(value::startsWith);
   }
}
