package com.aquabasilea.coursebooker.model.course;

import java.util.Arrays;

public enum CourseLocation {

   FITNESSPARK_WINTERTHUR("Fitnesspark Winterthur", "10"),

   FITNESSPARK_GLATTPARK("Fitnesspark Glattpark", "13"),

   FITNESSPARK_HEUWAAGE("Fitnesspark Heuwaage", "16"),

   FITNESSPARK_REGENSDORF("Fitnesspark Regensdorf", "18"),

   MIGROS_FITNESSCENTER_CLARASTRASSE("Migros Fitnesscenter Clarastrasse", "71"),

   MIGROS_FITNESSCENTER_FRENKENDORF("Migros Fitnesscenter Frenkendorf", "72"),

   MIGROS_FITNESSCENTER_AQUABASILEA("Migros Fitnesscenter Aquabasilea", "139"),

   MIGROS_FITNESSCENTER_NIEDERHOLZ("Migros Fitnesscenter Niederholz", "140");

   private final String courseLocationName;
   private final String id;

   CourseLocation(String name, String id) {
      this.courseLocationName = name;
      this.id = id;
   }

   /**
    * Returns a {@link CourseLocation} which <code>id</code> attribute matches with the provided id
    *
    * @param courseLocationId the id of the {@link CourseLocation}
    * @return a {@link CourseLocation} which <code>id</code> attribute matches with the provided id
    */
   public static CourseLocation fromId(String courseLocationId) {
      return Arrays.stream(CourseLocation.values())
              .filter(courseLocation -> courseLocation.id.equals(courseLocationId))
              .findFirst()
              .orElseThrow(() -> new IllegalStateException("There is no CourseLocation with course location id '" + courseLocationId + "'!"));
   }

   /**
    * Returns a {@link CourseLocation} which <code>courseLocationName</code> attribute matches with the provided value
    *
    * @param courseLocationName the display name of the {@link CourseLocation}
    * @return a {@link CourseLocation} which <code>courseLocationName</code> attribute matches with the provided value
    */
   public static CourseLocation fromDisplayName(String courseLocationName) {
      return Arrays.stream(CourseLocation.values())
              .filter(courseLocation -> courseLocation.courseLocationName.equals(courseLocationName))
              .findFirst()
              .orElseThrow(() -> new IllegalStateException("There is no CourseLocation with course location id '" + courseLocationName + "'!"));
   }

   public String getCourseLocationName() {
      return courseLocationName;
   }

   public String getId() {
      return id;
   }
}
