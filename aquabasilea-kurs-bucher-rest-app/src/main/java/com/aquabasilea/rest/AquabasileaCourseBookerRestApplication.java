package com.aquabasilea.rest;

import com.aquabasilea.coursebooker.AquabasileaCourseBooker;
import com.aquabasilea.exception.GlobalExceptionHandler;
import com.aquabasilea.security.securestorage.SecretStorage;
import com.aquabasilea.security.securestorage.util.KeyUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.function.Supplier;

import static com.aquabasilea.rest.config.AquabasileaCourseBookerRestAppConfig.AQUABASILEA_COURSE_BOOKER_BEAN;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class AquabasileaCourseBookerRestApplication {

   public static void main(String[] args) {
      Thread.setDefaultUncaughtExceptionHandler(new GlobalExceptionHandler());
      ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(AquabasileaCourseBookerRestApplication.class, args);
      AquabasileaCourseBooker aquabasileaCourseBooker = (AquabasileaCourseBooker) configurableApplicationContext.getBean(AQUABASILEA_COURSE_BOOKER_BEAN);
      doAuthentication(args, aquabasileaCourseBooker);
      aquabasileaCourseBooker.start();
   }

   private static void doAuthentication(String[] args, AquabasileaCourseBooker aquabasileaCourseBooker) {
      Supplier<char[]> userPasswordSupplier = new SecretStorage(KeyUtils.AQUABASILEA_KEYSTORAGE).getSecretSupplier4Alias(args[0], "".toCharArray());
      aquabasileaCourseBooker.onUserAuthenticated(args[0], userPasswordSupplier);
   }
}
