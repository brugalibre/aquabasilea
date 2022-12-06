package com.aquabasilea.model.course;

import java.util.Arrays;

public enum CourseLocation {

   FITNESSPARK_WINTERTHUR("Fitnesspark Winterthur", "10"),

   FITNESSPARK_GLATTPARK("Fitnesspark Glattpark", "13"),

   FITNESSPARK_HEUWAAGE("Fitnesspark Heuwaage", "16"),

   FITNESSPARK_REGENSDORF("Fitnesspark Regensdorf", "18"),

   MIGROS_FITNESSCENTER_CLARASTRASSE("Migros Fitnesscenter Clarastrasse", "71"),

   MIGROS_FITNESSCENTER_FRENKENDORF("72=Migros Fitnesscenter Frenkendorf", "72"),

   MIGROS_FITNESSCENTER_AQUABASILEA("Migros Fitnesscenter Aquabasilea", "139"),

   MIGROS_FITNESSCENTER_NIEDERHOLZ("Migros Fitnesscenter Niederholz", "140");

   private final String courseLocationName;
   private final String id;

   CourseLocation(String name, String id) {
      this.courseLocationName = name;
      this.id = id;
   }

   public static CourseLocation of(String courseLocationName) {
      return Arrays.stream(CourseLocation.values())
              .filter(courseLocation -> courseLocation.courseLocationName.equals(courseLocationName))
              .findFirst()
              .orElseThrow(() -> new IllegalStateException("There is no CourseLocation with course location name '" + courseLocationName + "'!"));
   }

   public String getCourseLocationName() {
      return courseLocationName;
   }

   public String getId() {
      return id;
   }
}
