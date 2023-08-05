package com.aquabasilea.domain.userconfig.model;

import com.aquabasilea.domain.course.CourseLocation;
import com.brugalibre.domain.user.model.User;

import java.util.List;

/**
 * The {@link DefaultUserConfig} contains some hard coded default configuration values
 */
public class DefaultUserConfig {
   private DefaultUserConfig() {
      // private
   }

   /**
    * The default {@link CourseLocation}s for each {@link User}
    */
   public static final List<CourseLocation> COURSE_LOCATIONS = List.of(CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA,
           CourseLocation.FITNESSPARK_HEUWAAGE,
           CourseLocation.MIGROS_FITNESSCENTER_FRENKENDORF);
}
