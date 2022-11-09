package com.aquabasilea.util;

public class SleepUtil {
   private SleepUtil() {
      // private
   }

   public static void sleep(int timeInMillis) {
      try {
         Thread.sleep(timeInMillis);
      } catch (InterruptedException e) {
         // ignore
      }
   }
}
