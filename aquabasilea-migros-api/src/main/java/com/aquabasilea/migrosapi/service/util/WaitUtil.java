package com.aquabasilea.migrosapi.service.util;

public class WaitUtil {

   /**
    * Suspends the current thread and throws an unchecked {@link Exception} if interrupted
    *
    * @param timeout
    */
   public static void suspendCurrentThread(long timeout) {
      try {
         Thread.sleep(timeout);
      } catch (InterruptedException e) {
         throw new RuntimeException(e);
      }
   }
}
