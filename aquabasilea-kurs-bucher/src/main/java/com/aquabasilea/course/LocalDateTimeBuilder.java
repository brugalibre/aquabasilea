package com.aquabasilea.course;

import com.aquabasilea.util.DateUtil;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoField;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

public class LocalDateTimeBuilder {

   private static final String TIME_OF_THE_DAY_PATTERN = "hh:mm";
   private static final String TIME_OF_THE_DAY_REGEX = "([0-9]{1,2}[:][0-9]{2})";

   private LocalDateTimeBuilder() {
      // private
   }

   /**
    * Creates a new {@link LocalDateTime} object from the given day-of-the-week and the time
    * Note: The <code>timeOfTheDay</code> must be formatted like hh:mm whereas the <code>dayOfTheWeek</code>
    * is a string literal, describing the current day in english
    *
    * @param courseDateAsString the day when the course takes place as string value
    * @param timeOfTheDay       the time, when the course starts as string value
    * @return
    */
   public static LocalDateTime createCourseDate(String courseDateAsString, String timeOfTheDay) {
      return getLocalDateTimeWithReferenceDate(LocalDateTime.now(), courseDateAsString, timeOfTheDay);
   }

   static LocalDateTime getLocalDateTimeWithReferenceDate(LocalDateTime refDateIn, String courseDateAsString, String timeOfTheDay) {
      validateInput(courseDateAsString, timeOfTheDay);
      DayOfWeek courseDate = DayOfWeek.valueOf(courseDateAsString.toUpperCase());
      int courseHour = Integer.parseInt(timeOfTheDay.substring(0, timeOfTheDay.indexOf(':')));
      int courseMin = Integer.parseInt(timeOfTheDay.substring(timeOfTheDay.indexOf(':') + 1));
      LocalDateTime refDate = adjustReferenceDateIfCourseTimeIsBeyondRefDateTime(refDateIn, courseDate, courseHour, courseMin);
      int dayOfMonth = getDayOfMonthAtWhichCourseTakesPlace(refDate, courseDate);

      int month = getMonth(refDate, dayOfMonth);
      int year = getYear(refDate, month);
      return LocalDateTime.of(year, month, dayOfMonth, courseHour, courseMin);
   }

   private static LocalDateTime adjustReferenceDateIfCourseTimeIsBeyondRefDateTime(LocalDateTime refDate, DayOfWeek courseDate, int courseHour, int courseMin) {
      if (isSameDayOfTheWeekAndCourseTimeIsEarlierThanRefTime(refDate, courseDate, courseHour, courseMin)) {
         refDate = refDate.plusDays(1);
      }
      return refDate;
   }

   private static boolean isSameDayOfTheWeekAndCourseTimeIsEarlierThanRefTime(LocalDateTime refDate, DayOfWeek courseDate, int courseHour, int courseMin) {
      return courseDate == refDate.getDayOfWeek()
              && courseHour < refDate.getHour() || (courseHour == refDate.getHour()
              && courseMin <= refDate.getMinute());
   }

   /**
    * gets the new day of the month, given our reference date and the day, on which the course takes place
    */
   private static int getDayOfMonthAtWhichCourseTakesPlace(LocalDateTime refDate, DayOfWeek courseDate) {
      int currentDayOfWeek = refDate.get(ChronoField.DAY_OF_WEEK);
      int diff = courseDate.getValue() - currentDayOfWeek;
      int lastDayOfMonth = DateUtil.getLastDayOfMonth(refDate);
      if (diff < 0) {
         diff = DayOfWeek.values().length + diff;
      }
      int newDayOfMonth = refDate.getDayOfMonth() + diff;
      if (newDayOfMonth > lastDayOfMonth) {
         return newDayOfMonth - lastDayOfMonth;
      }
      return newDayOfMonth;
   }

   private static int getYear(LocalDateTime refDate, int newMonth) {
      int currentYear = refDate.getYear();
      if (newMonth < refDate.getMonth().getValue()) {
         // the new month is smaller -> overflow at december -> January
         return currentYear + 1;
      }
      return currentYear;
   }

   private static int getMonth(LocalDateTime refDate, int newDayOfMonth) {
      int newMonth = refDate.getMonth().getValue();
      if (newDayOfMonth < refDate.getDayOfMonth()) {
         // the new day in the month is smaller -> overflow at the end of the month, while adding new days -> start over at 1
         newMonth = newMonth + 1;
      }
      if (newMonth > Month.values().length) {
         return Month.JANUARY.getValue();
      }
      return newMonth;
   }

   private static void validateInput(String dayOfWeek, String timeOfTheDay) {
      requireNonNull(dayOfWeek, "Attribut 'dayOfWeek' must be set!");
      requireNonNull(timeOfTheDay, "Attribut 'timeOfTheDay' must be set!");
      Pattern pattern = Pattern.compile(TIME_OF_THE_DAY_REGEX);
      Matcher matcher = pattern.matcher(timeOfTheDay);
      if (!matcher.matches()) {
         throw new IllegalStateException("The time input '" + timeOfTheDay + "' does not match the required pattern '" + TIME_OF_THE_DAY_PATTERN + "'");
      }
   }
}
