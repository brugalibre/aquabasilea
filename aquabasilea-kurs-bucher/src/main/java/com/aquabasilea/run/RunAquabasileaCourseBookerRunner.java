package com.aquabasilea.run;

import com.aquabasilea.coursebooker.AquabasileaCourseBooker;
import com.aquabasilea.systemtray.AquabasileaCourseBookerSystemTray;
import com.aquabasilea.systemtray.icons.ImageLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunAquabasileaCourseBookerRunner {

   private static final Logger LOG = LoggerFactory.getLogger(RunAquabasileaCourseBookerRunner.class);

   public static void main(String[] args) {
      if (args.length < 2) {
         LOG.error("Programm needs username and password!\nUsage: java -jar RunAquabasileaCourseBookerRunner.jar <username> <password>");
         System.exit(-1);
      }
      ImageLibrary.loadPictures();
      AquabasileaCourseBooker aquabasileaCourseBooker = new AquabasileaCourseBooker(args[0], args[1], Thread.currentThread());
      AquabasileaCourseBookerSystemTray aquabasileaCourseBookerSystemTray = buildAquabasileaCourseBookerSystemTray(aquabasileaCourseBooker);
      aquabasileaCourseBooker.addCourseBookingStateChangedHandler(aquabasileaCourseBookerSystemTray);
      aquabasileaCourseBooker.run();
   }

   private static AquabasileaCourseBookerSystemTray buildAquabasileaCourseBookerSystemTray(AquabasileaCourseBooker aquabasileaCourseBooker) {
      AquabasileaCourseBookerSystemTray aquabasileaCourseBookerSystemTray = new AquabasileaCourseBookerSystemTray();
      aquabasileaCourseBookerSystemTray.registerSystemtray(aquabasileaCourseBooker);
      return aquabasileaCourseBookerSystemTray;
   }
}
