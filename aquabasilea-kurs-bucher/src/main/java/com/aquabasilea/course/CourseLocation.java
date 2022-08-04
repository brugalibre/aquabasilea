package com.aquabasilea.course;

import java.util.Arrays;

public enum CourseLocation {
   MIGROS_FITNESSCENTER_AQUABASILEA("Migros Fitnesscenter Aquabasilea"),

   MIGROS_FITNESSCENTER_NIEDERHOLZ("Migros Fitnesscenter Niederholz"),

   MIGROS_FITNESSCENTER_CLARASTRASSE("Migros Fitnesscenter Clarastrasse"),

   MIGROS_FITNESSCENTER_FRENKENDORF("Migros Fitnesscenter Frenkendorf"),

   FITNESSPARK_HEUWAAGE("Fitnesspark Heuwaage"),

   FITNESSPARK_NATIONAL("Fitnesspark National"),

   FITNESSPARK_SIHLCITY("Fitnesspark Sihlcity"),

   FITNESSPARK_TIME_OUT("Fitnesspark Time-Out"),

   FITNESSPARK_PULS5("Fitnesspark Puls5"),

   FITNESSPARK_STADELHOFEN("Fitnesspark Stadelhofen"),

   FITNESSPARK_TRAFO_BADEN("Fitnesspark Trafo Baden"),

   FITNESSPARK_REGENSDORF("Fitnesspark Regensdorf"),

   FITNESSPARK_STOCKERHOF("Fitnesspark Stockerhof"),

   FITNESSPARK_WINTERTHUR("Fitnesspark Winterthur");

   private final String courseLocationName;

   CourseLocation(String name) {
      this.courseLocationName = name;
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

   public com.aquabasilea.web.model.CourseLocation getWebCourseLocation() {
      return com.aquabasilea.web.model.CourseLocation.forCourseLocationName(this.courseLocationName);
   }
}
