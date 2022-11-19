package com.aquabasilea.web.run;

import com.aquabasilea.web.bookcourse.AquabasileaWebCourseBooker;
import com.aquabasilea.web.bookcourse.impl.AquabasileaWebCourseBookerImpl;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseBookingEndResult;
import com.aquabasilea.web.bookcourse.model.CourseBookDetails;
import com.aquabasilea.web.model.CourseLocation;

import java.time.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

public class RunAquabasileaNavigator {

   public static void main(String[] args) {

      try {
         run(args);
      } catch (Exception ex) {
         System.err.println(ex);
      }
   }

   private static void run(String[] args) {
      String courseName = "Aqua Tabata 50 Min.";
      String courseInstructor = "Sibylle A.";
      String username = args[0];
      String password = args[1];
      LocalDateTime courseDateAndTime = getCourseDateAndTime();

      boolean dryRun = true;
      if (args.length >= 3) {
         dryRun = Boolean.parseBoolean(args[2]);
      }
      AquabasileaWebCourseBooker aquabasileaWebCourseBooker = AquabasileaWebCourseBookerImpl.createAndInitAquabasileaWebNavigator(username, password, dryRun, getDurationUntilIsBookableSupplier());
      CourseBookingEndResult courseBookingEndResult = aquabasileaWebCourseBooker.selectAndBookCourse(new CourseBookDetails(courseName, courseInstructor, courseDateAndTime, CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA));
      printErrors(dryRun, courseBookingEndResult);
   }

   private static LocalDateTime getCourseDateAndTime() {
      LocalDate now = LocalDate.now();
      LocalDate date = LocalDate.of(now.getYear(), now.getMonth(), now.getDayOfMonth() + 1);
      return LocalDateTime.of(date, LocalTime.of(12, 15));
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
