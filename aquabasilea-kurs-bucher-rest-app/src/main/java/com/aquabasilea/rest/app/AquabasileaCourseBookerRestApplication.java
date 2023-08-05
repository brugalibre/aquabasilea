package com.aquabasilea.rest.app;

import com.aquabasilea.application.exception.GlobalExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class AquabasileaCourseBookerRestApplication {

   public static void main(String[] args) {
      Thread.setDefaultUncaughtExceptionHandler(new GlobalExceptionHandler());
      SpringApplication.run(AquabasileaCourseBookerRestApplication.class, args);
   }
}
