package com.aquabasilea.model.course;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
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
    * Creates a new {@link LocalDateTime} object from the given {@link DayOfWeek} and the time of the day.
    * If the calculated {@link LocalDateTime} is already in the past (compared <code>{@link LocalDateTime#now()}</code>) then the reference
    * day is shifted one day into the future
    *
    * @param dayOfWeek    the day when the course takes place as string value
    * @param timeOfTheDay the time, when the course starts as string value
    * @return a new {@link LocalDateTime} object from the given day-of-the-week and the time
    */
   public static LocalDateTime createLocalDateTime(DayOfWeek dayOfWeek, String timeOfTheDay) {
      return createLocalDateTimeWithReferenceDate(LocalDateTime.now(), dayOfWeek, timeOfTheDay);
   }

   static LocalDateTime createLocalDateTimeWithReferenceDate(LocalDateTime refDateIn, DayOfWeek courseDate, String timeOfTheDay) {
      validateInput(courseDate, timeOfTheDay);
      LocalTime localTime = createLocalTime(timeOfTheDay);
      int courseHour = localTime.getHour();
      int courseMin = localTime.getMinute();
      LocalDateTime refDate = adjustReferenceDateIfCourseTimeIsBeyondRefDateTime(refDateIn, courseDate, courseHour, courseMin);
      int dayOfMonth = refDate.getDayOfMonth();
      int month = getMonth(refDate, dayOfMonth);
      int year = getYear(refDate, month);
      return LocalDateTime.of(year, month, refDate.getDayOfMonth(), courseHour, courseMin);
   }

   private static LocalDateTime adjustReferenceDateIfCourseTimeIsBeyondRefDateTime(LocalDateTime refDate, DayOfWeek courseDate, int courseHour, int courseMin) {
      if (isSameDayOfTheWeekAndCourseTimeIsEarlierThanRefTime(refDate, courseDate, courseHour, courseMin)) {
         refDate = refDate.plusDays(1);
      }
      return refDate;
   }

   /**
    * Creates a {@link LocalTime} from the given input. This input must follow the pattern {@link LocalDateTimeBuilder#TIME_OF_THE_DAY_PATTERN}
    *
    * @param timeOfTheDay the input value
    * @return a {@link LocalTime} instance
    */
   public static LocalTime createLocalTime(String timeOfTheDay) {
      validateLocaleTimeInput(timeOfTheDay);
      int hour = Integer.parseInt(timeOfTheDay.substring(0, timeOfTheDay.indexOf(":")));
      int min = Integer.parseInt(timeOfTheDay.substring(timeOfTheDay.indexOf(":") + 1));
      return LocalTime.of(hour, min);
   }

   private static boolean isSameDayOfTheWeekAndCourseTimeIsEarlierThanRefTime(LocalDateTime refDate, DayOfWeek courseDate, int courseHour, int courseMin) {
      return courseDate == refDate.getDayOfWeek()
              && (courseHour < refDate.getHour() || (courseHour == refDate.getHour()
              && courseMin <= refDate.getMinute()));
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

   private static void validateInput(DayOfWeek dayOfWeek, String timeOfTheDay) {
      requireNonNull(dayOfWeek, "Attribut 'dayOfWeek' must be set!");
      validateLocaleTimeInput(timeOfTheDay);
   }

   private static void validateLocaleTimeInput(String timeOfTheDay) {
      requireNonNull(timeOfTheDay, "Attribut 'timeOfTheDay' must be set!");
      Pattern pattern = Pattern.compile(TIME_OF_THE_DAY_REGEX);
      Matcher matcher = pattern.matcher(timeOfTheDay);
      if (!matcher.matches()) {
         throw new IllegalStateException("The time input '" + timeOfTheDay + "' does not match the required pattern '" + TIME_OF_THE_DAY_PATTERN + "'");
      }
   }
}
