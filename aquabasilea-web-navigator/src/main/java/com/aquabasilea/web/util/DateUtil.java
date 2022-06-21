package com.aquabasilea.web.util;

import org.apache.commons.lang.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

public class DateUtil {

   private static final String TIME_OF_THE_DAY_PATTERN = "hh:mm";
   private static final String TIME_OF_THE_DAY_REGEX = "([0-9]{1,2}[:][0-9]{2})";
   private static final String DAY_OF_THE_WEEK_SEPARATOR = " ";

   private DateUtil() {
      // private
   }


   /**
    * Creates a {@link LocalTime} from the given input. This input must follow the pattern {@link DateUtil#TIME_OF_THE_DAY_PATTERN}
    *
    * @param timeOfTheDay the input value
    * @return a {@link LocalTime} instance
    */
   public static LocalTime getLocalTimeFromInput(String timeOfTheDay) {
      validateLocaleTimeInput(timeOfTheDay);
      int hour = Integer.parseInt(timeOfTheDay.substring(0, timeOfTheDay.indexOf(":")));
      int min = Integer.parseInt(timeOfTheDay.substring(timeOfTheDay.indexOf(":") + 1));
      return LocalTime.of(hour, min);
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

   private static void validateLocaleTimeInput(String timeOfTheDay) {
      requireNonNull(timeOfTheDay, "Attribut 'timeOfTheDay' must be set!");
      Pattern pattern = Pattern.compile(TIME_OF_THE_DAY_REGEX);
      Matcher matcher = pattern.matcher(timeOfTheDay);
      if (!matcher.matches()) {
         throw new IllegalStateException("The time input '" + timeOfTheDay + "' does not match the required pattern '" + TIME_OF_THE_DAY_PATTERN + "'");
      }
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
