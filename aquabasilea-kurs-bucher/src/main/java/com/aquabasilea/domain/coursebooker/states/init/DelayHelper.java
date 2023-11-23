package com.aquabasilea.domain.coursebooker.states.init;

import java.time.Duration;

public class DelayHelper {

   private final Duration maxDelay;

   public DelayHelper(Duration maxDelay) {
      this.maxDelay = maxDelay;
   }

   /**
    * Creates a random delay between 0 and +-<code>maxDelay</code> seconds
    *
    * @return a random delay between 0 and +-<code>maxDelay</code> seconds
    */
   public long getRandomDelay() {
      long randomDelay = (long) (Math.random() * maxDelay.getSeconds());
      int sign = Math.random() > 0.5 ? 1 : -1;
      return randomDelay * sign;
   }
}
