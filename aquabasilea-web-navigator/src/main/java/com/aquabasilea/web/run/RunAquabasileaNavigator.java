package com.aquabasilea.web.run;

import com.aquabasilea.web.bookcourse.AquabasileaWebCourseBooker;
import com.aquabasilea.web.bookcourse.impl.AquabasileaWebCourseBookerImpl;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseBookingEndResult;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseClickedResult;
import com.aquabasilea.web.bookcourse.model.CourseBookDetails;
import com.aquabasilea.web.model.CourseLocation;

import java.time.DayOfWeek;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

public class RunAquabasileaNavigator {

   public static void main(String[] args) {

      String courseName = "TRX 45 Min.";
      String username = args[0];
      String password = args[1];
      DayOfWeek dayOfWeek = DayOfWeek.SUNDAY;

      boolean dryRun = false;
      if (args.length >= 3) {
         dryRun = Boolean.parseBoolean(args[2]);
      }
      AquabasileaWebCourseBooker aquabasileaWebCourseBooker = AquabasileaWebCourseBookerImpl.createAndInitAquabasileaWebNavigator(username, password, dryRun, getDurationUntilIsBookableSupplier());
      CourseBookingEndResult courseBookingEndResult = aquabasileaWebCourseBooker.selectAndBookCourse(new CourseBookDetails(courseName, dayOfWeek, CourseLocation.FITNESSPARK_HEUWAAGE));
      printErrors(dryRun, courseBookingEndResult);
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
