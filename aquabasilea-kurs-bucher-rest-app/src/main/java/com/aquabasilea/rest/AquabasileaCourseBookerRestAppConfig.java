package com.aquabasilea.rest;

import com.aquabasilea.coursebooker.AquabasileaCourseBooker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AquabasileaCourseBookerRestAppConfig {

   @Bean
   public AquabasileaCourseBooker getAquabasileaCourseBooker() {
      return AquabasileaCourseBookerRestApplication.aquabasileaCourseBooker;
   }
}
