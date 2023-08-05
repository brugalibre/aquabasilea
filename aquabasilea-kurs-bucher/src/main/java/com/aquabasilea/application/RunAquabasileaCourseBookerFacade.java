package com.aquabasilea.application;

import com.aquabasilea.domain.coursebooker.booking.apimigros.MigrosApiProvider;
import com.aquabasilea.domain.coursebooker.booking.facade.AquabasileaCourseBookerFacadeFactory;
import com.aquabasilea.domain.coursebooker.states.booking.facade.AquabasileaCourseBookerFacade;
import com.aquabasilea.domain.coursebooker.states.booking.facade.BookingContext;
import com.aquabasilea.domain.coursebooker.states.booking.facade.CourseBookContainer;
import com.aquabasilea.util.DateUtil;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseBookingEndResult;
import com.aquabasilea.web.bookcourse.model.CourseBookDetails;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.Supplier;

public class RunAquabasileaCourseBookerFacade {

   public static void main(String[] args) {

      String courseName = "Indoor Cycling 50' G2";
//      String courseName = "Pilates Faszial 50' G1";// ausgebucht
//      String courseName = "Bodytone 50' G2";// noch nicht buchbar
      String courseLocationName = "Fitnesspark Heuwaage";
//      String courseLocationName = "Migros Fitnesscenter Aquabasilea";
      String username = args[0];
      char[] password = args[1].toCharArray();
      LocalDateTime courseDateAndTime = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(14, 57));

      boolean dryRun = true;
      if (args.length >= 3) {
         dryRun = Boolean.parseBoolean(args[2]);
      }
      Supplier<Duration> durationUntilIsBookableSupplier = getDurationUntilIsBookableSupplier(courseDateAndTime);
      AquabasileaCourseBookerFacadeFactory aquabasileaCourseBookerFacadeFactory = new AquabasileaCourseBookerFacadeFactory(new MigrosApiProvider());
      AquabasileaCourseBookerFacade aquabasileaCourseBookerFacade = aquabasileaCourseBookerFacadeFactory.createNewAquabasileaCourseBookerFacade(username, () -> password, durationUntilIsBookableSupplier);
      long start = System.currentTimeMillis();
      CourseBookContainer courseBookContainer = getCourseBookContainer(courseName, courseLocationName, courseDateAndTime, dryRun);
      CourseBookingEndResult courseBookingEndResult = aquabasileaCourseBookerFacade.selectAndBookCourse(courseBookContainer);
      System.out.println("Booker done, duration: " + Duration.ofMillis(start - System.currentTimeMillis()));
      printErrors(dryRun, courseBookingEndResult);
   }

   private static CourseBookContainer getCourseBookContainer(String courseName, String courseLocationName, LocalDateTime courseDateAndTime, boolean dryRun) {
      CourseBookDetails courseBookDetails = new CourseBookDetails(courseName, "", courseDateAndTime, courseLocationName);
      return new CourseBookContainer(courseBookDetails, new BookingContext(dryRun));
   }

   private static Supplier<Duration> getDurationUntilIsBookableSupplier(LocalDateTime courseDate) {
      return () -> {
         long timeLeft = DateUtil.calcTimeLeftBeforeDate(courseDate);
         return Duration.ofMillis(timeLeft);
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
