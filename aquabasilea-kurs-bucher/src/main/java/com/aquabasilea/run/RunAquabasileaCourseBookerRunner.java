package com.aquabasilea.run;

import com.aquabasilea.coursebooker.AquabasileaCourseBooker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunAquabasileaCourseBookerRunner {

   private static final Logger LOG = LoggerFactory.getLogger(RunAquabasileaCourseBookerRunner.class);

   public static void main(String[] args) {
      if (args.length < 2) {
         LOG.error("Programm needs username and password!\nUsage: java -jar RunAquabasileaCourseBookerRunner.jar <username> <password>");
         System.exit(-1);
      }
      AquabasileaCourseBooker aquabasileaCourseBooker = new AquabasileaCourseBooker(args[0], args[1]);
      aquabasileaCourseBooker.run();
   }
}
