package com.aquabasilea.domain.userconfig.model;

import com.aquabasilea.domain.courselocation.model.CourseLocation;

import java.util.List;

/**
 * The {@link DefaultUserConfig} contains some hard coded default configuration values
 */
public class DefaultUserConfig {
   private DefaultUserConfig() {
      // private
   }

   /**
    * The center-ids of the defalut {@link CourseLocation}s
    */
   public static final List<String> DEFAULT_COURSE_LOCATION_CENTER_IDS = List.of("139", "16");
}
