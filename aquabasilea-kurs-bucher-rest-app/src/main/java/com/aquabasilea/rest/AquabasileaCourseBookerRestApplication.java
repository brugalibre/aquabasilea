package com.aquabasilea.rest;

import com.aquabasilea.alerting.consumer.impl.CourseBookingEndResultConsumerImpl;
import com.aquabasilea.coursebooker.AquabasileaCourseBooker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AquabasileaCourseBookerRestApplication {

   static AquabasileaCourseBooker aquabasileaCourseBooker;

   public static void main(String[] args) {
      AquabasileaCourseBookerRestApplication.aquabasileaCourseBooker = buildAquabasileaCourseBooker(args[0], args[1], Thread.currentThread());
      SpringApplication.run(AquabasileaCourseBookerRestApplication.class, args);
      aquabasileaCourseBooker.run();
   }

   public static AquabasileaCourseBooker buildAquabasileaCourseBooker(String username, String password, Thread thread) {
      AquabasileaCourseBooker aquabasileaCourseBooker = new AquabasileaCourseBooker(username, password, thread);
      aquabasileaCourseBooker.addCourseBookingEndResultConsumer(new CourseBookingEndResultConsumerImpl());
      return aquabasileaCourseBooker;
   }
}
