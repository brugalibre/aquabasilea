package com.aquabasilea.web.run;

import com.aquabasilea.web.extractcourses.AquabasileaCourseExtractor;
import com.aquabasilea.web.extractcourses.impl.AquabasileaCourseExtractorImpl;
import com.aquabasilea.web.extractcourses.model.AquabasileaCourse;
import com.aquabasilea.web.extractcourses.model.ExtractedAquabasileaCourses;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;

public class RunAquabasileaExtractor {

   private static final String DEBUG_CONFIG_FILE = "config/debug-aquabasilea-kurs-bucher-config.yml";

   public static void main(String[] args) {
      AquabasileaCourseExtractor aquabasileaWebNavigator = AquabasileaCourseExtractorImpl.createAndInitAquabasileaWebNavigator(DEBUG_CONFIG_FILE);
      long start = System.currentTimeMillis();
      ExtractedAquabasileaCourses extractedAquabasileaCourses = aquabasileaWebNavigator.extractAquabasileaCourses(List.of("Migros Fitnesscenter Clarastrasse", "Fitnesspark Heuwaage", "Migros Fitnesscenter Aquabasilea"));
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
