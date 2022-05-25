package com.aquabasilea.web.run;

import com.aquabasilea.web.extractcourses.AquabasileaCourseExtractor;
import com.aquabasilea.web.extractcourses.impl.AquabasileaCourseExtractorImpl;
import com.aquabasilea.web.extractcourses.model.AquabasileaCourse;
import com.aquabasilea.web.extractcourses.model.ExtractedAquabasileaCourses;
import com.aquabasilea.web.model.CourseLocation;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;

public class RunAquabasileaExtractor {

   public static void main(String[] args) {
      AquabasileaCourseExtractor aquabasileaWebNavigator = AquabasileaCourseExtractorImpl.createAndInitAquabasileaWebNavigator();
      long start = System.currentTimeMillis();
      ExtractedAquabasileaCourses extractedAquabasileaCourses = aquabasileaWebNavigator.extractAquabasileaCourses(List.of(CourseLocation.MIGROS_FITNESSCENTER_CLARASTRASSE, CourseLocation.FITNESSPARK_HEUWAAGE, CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA));
      Duration duration = Duration.ofMillis(System.currentTimeMillis() - start);
      System.err.println("Kurse extrahiert, benötigte Zeit: " + duration.toMinutes() + "min. und " + duration.getSeconds() + "s");
      logExtractedCourses(extractedAquabasileaCourses);
   }

   private static void logExtractedCourses(ExtractedAquabasileaCourses extractedAquabasileaCourses) {
      System.out.println("Gefundene Kurse: " + extractedAquabasileaCourses.getAquabasileaCourses().size());
      extractedAquabasileaCourses.getAquabasileaCourses().sort(Comparator.comparing(AquabasileaCourse::courseLocation));
      for (AquabasileaCourse aquabasileaCourse : extractedAquabasileaCourses.getAquabasileaCourses()) {
         System.out.println("Kurs: " + aquabasileaCourse);
      }
   }
}
