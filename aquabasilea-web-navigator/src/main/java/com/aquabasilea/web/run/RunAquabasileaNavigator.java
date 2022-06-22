package com.aquabasilea.web.run;

import com.aquabasilea.web.navigate.AquabasileaWebNavigator;
import com.aquabasilea.web.navigate.AquabasileaWebNavigatorImpl;
import com.aquabasilea.web.selectcourse.result.CourseBookingEndResult;
import com.aquabasilea.web.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongSupplier;

public class RunAquabasileaNavigator {
   private static final Logger LOG = LoggerFactory.getLogger(RunAquabasileaNavigator.class);

   public static void main(String[] args) {

      String courseName = "Indoor Cycling";
      String username = args[0];
      String password = args[1];
      DayOfWeek dayOfWeek = getDayOfWeekFromInput(args[2]);

      boolean dryRun = false;
      if (args.length >= 4) {
         dryRun = Boolean.parseBoolean(args[3]);
      }
      AquabasileaWebNavigator aquabasileaWebNavigator = AquabasileaWebNavigatorImpl.createAndInitAquabasileaWebNavigator(username, password, dryRun, getTimeUntilIsBookableSupplier());
      CourseBookingEndResult courseBookingEndResult = aquabasileaWebNavigator.selectAndBookCourse(courseName, dayOfWeek);
      printErrors(courseBookingEndResult);
   }

   private static LongSupplier getTimeUntilIsBookableSupplier() {
      final AtomicLong timeOut = new AtomicLong(13300);
      return () -> {
         timeOut.set(timeOut.get() - 3300);
         return timeOut.get();
      };
   }

   private static void printErrors(CourseBookingEndResult courseBookingEndResult) {
      if (!courseBookingEndResult.getErrors().isEmpty()) {
         System.err.println("There where errors:");
         for (String error : courseBookingEndResult.getErrors()) {
            System.err.println(error);
         }
      } else {
         System.out.println("App completed normally");
      }
      System.out.println("Course selected result: " + courseBookingEndResult.getCourseClickedResult());
   }

   private static DayOfWeek getDayOfWeekFromInput(String arg) {
      DayOfWeek dayOfWeekFromInput = DateUtil.getDayOfWeekFromInput(arg, Locale.GERMAN);
      if (Objects.isNull(dayOfWeekFromInput)) {
         LOG.error("Programm needs the date when the course takes place!!\nUsage: java -jar RunAquabasileaNavigator.jar <username> <password> <Freitag>");
         System.exit(-1);
      }
      return dayOfWeekFromInput;
   }
}
