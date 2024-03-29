package com.aquabasilea.web.run;

import com.aquabasilea.web.bookcourse.AquabasileaWebCourseBooker;
import com.aquabasilea.web.bookcourse.impl.AquabasileaWebCourseBookerImpl;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseBookingEndResult;
import com.aquabasilea.web.bookcourse.model.CourseBookDetails;

import java.time.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

public class RunAquabasileaNavigator {

   private static final String DEBUG_CONFIG_FILE = "config/debug-aquabasilea-kurs-bucher-config.yml";

   public static void main(String[] args) {

      try {
         run(args);
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   private static void run(String[] args) {
      String courseName = "Functional Grouptraining 50'";
      String courseInstructor = "EricS";
      String username = args[0];
      char[] password = args[1].toCharArray();
      LocalDateTime courseDateAndTime = getCourseDateAndTime();

      boolean dryRun = true;
      if (args.length >= 3) {
         dryRun = Boolean.parseBoolean(args[2]);
      }
      AquabasileaWebCourseBooker aquabasileaWebCourseBooker = AquabasileaWebCourseBookerImpl.createAndInitAquabasileaWebNavigator(username, password, dryRun, getDurationUntilIsBookableSupplier(), DEBUG_CONFIG_FILE);
      long start = System.currentTimeMillis();
      CourseBookingEndResult courseBookingEndResult = aquabasileaWebCourseBooker.selectAndBookCourse(new CourseBookDetails(courseName, courseInstructor, courseDateAndTime, "Fitnesspark Glattpark"));
      System.out.println("Booker done, duration: " + Duration.ofMillis(start - System.currentTimeMillis()));
      printErrors(dryRun, courseBookingEndResult);
   }

   private static LocalDateTime getCourseDateAndTime() {
      LocalDate now = LocalDate.now();
      LocalDate date = LocalDate.of(now.getYear(), now.getMonth(), now.getDayOfMonth() + 1);
      return LocalDateTime.of(date, LocalTime.of(19, 15));
   }

   private static Supplier<Duration> getDurationUntilIsBookableSupplier() {
      final AtomicLong timeOut = new AtomicLong(13300);
      return () -> {
         timeOut.set(timeOut.get() - 3300);
         return Duration.ofMillis(timeOut.get());
      };
   }

   private static void printErrors(boolean dryRun, CourseBookingEndResult courseBookingEndResult) {
      System.out.println("\n\n===================");
      System.out.println("Course selected result: " + courseBookingEndResult.getCourseClickedResult());
      if (!courseBookingEndResult.getErrors().isEmpty()) {
         System.err.println("\nThere where " + (dryRun ? "warnings:" : "errors:"));
         for (String error : courseBookingEndResult.getErrors()) {
            System.err.println(error);
         }
      } else {
         System.out.println("App completed normally");
      }
      if (!courseBookingEndResult.getErrors().isEmpty()) {
         try {
            Thread.sleep(999999);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
   }
}
