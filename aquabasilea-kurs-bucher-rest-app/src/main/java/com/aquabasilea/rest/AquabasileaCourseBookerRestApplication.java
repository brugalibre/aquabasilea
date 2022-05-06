package com.aquabasilea.rest;

import com.aquabasilea.coursebooker.AquabasileaCourseBooker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static com.aquabasilea.rest.AquabasileaCourseBookerRestAppConfig.AQUABASILEA_COURSE_BOOKER_BEAN;

@SpringBootApplication
public class AquabasileaCourseBookerRestApplication {

   public static void main(String[] args) {
      ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(AquabasileaCourseBookerRestApplication.class, args);
      AquabasileaCourseBooker aquabasileaCourseBooker = (AquabasileaCourseBooker) configurableApplicationContext.getBean(AQUABASILEA_COURSE_BOOKER_BEAN);
      aquabasileaCourseBooker.onUserAuthenticated(args[0], () -> args[1].toCharArray());
      aquabasileaCourseBooker.start();
   }
}
