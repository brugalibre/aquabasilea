package com.aquabasilea.app.systemtray;

import com.aquabasilea.alerting.consumer.impl.CourseBookingEndResultConsumerImpl;
import com.aquabasilea.coursebooker.AquabasileaCourseBooker;

public class AquabasileaCourseBookerApplication {

   private AquabasileaCourseBookerApplication (){
     // private
   }

   /**
    * Creates a new {@link AquabasileaCourseBooker} as well as a {@link AquabasileaCourseBookerSystemTray}
    * Note: This method call will also create and install a system traySt
    * @return the new created {@link AquabasileaCourseBooker}
    */
   public static AquabasileaCourseBooker createAquabasileaCourseBookerAndSystemTray(String username, String password) {
      AquabasileaCourseBooker aquabasileaCourseBooker = new AquabasileaCourseBooker(username, password, Thread.currentThread());
      AquabasileaCourseBookerSystemTray aquabasileaCourseBookerSystemTray = buildAquabasileaCourseBookerSystemTray(aquabasileaCourseBooker);
      aquabasileaCourseBooker.addCourseBookingStateChangedHandler(aquabasileaCourseBookerSystemTray);
      aquabasileaCourseBooker.addCourseBookingEndResultConsumer(new CourseBookingEndResultConsumerImpl());
      return aquabasileaCourseBooker;
   }

   private static AquabasileaCourseBookerSystemTray buildAquabasileaCourseBookerSystemTray(AquabasileaCourseBooker aquabasileaCourseBooker) {
      AquabasileaCourseBookerSystemTray aquabasileaCourseBookerSystemTray = new AquabasileaCourseBookerSystemTray();
      aquabasileaCourseBookerSystemTray.registerSystemtray(aquabasileaCourseBooker);
      return aquabasileaCourseBookerSystemTray;
   }
}
