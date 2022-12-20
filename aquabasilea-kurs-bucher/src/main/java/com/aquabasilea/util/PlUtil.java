package com.aquabasilea.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Stack;

/**
 * The Performance-Logging Util ({@link PlUtil}) contains utility methods for starting and stopping a performance logging
 */
public class PlUtil {

   public static final PlUtil INSTANCE = new PlUtil();
   private static final Stack<TslEntry> DURATION_STACK = new Stack<>();
   private static final Logger LOG = LoggerFactory.getLogger(PlUtil.class);

   private PlUtil() {
      // privé
   }

   /**
    * Logs the given message with {@link org.slf4j.event.Level#INFO} using the given logger and starts also a time stamp
    * This time stamp is used to indicate a duration between to logged messages
    *
    * @param msg the message to LOG
    * @see PlUtil#endLogInfo()
    */
   public void startLogInfo(String msg) {
      startLogInfoInternal(msg);
   }

   private void startLogInfoInternal(String name) {
      DURATION_STACK.push(new TslEntry(name, System.currentTimeMillis()));
      LOG.info("Start " + name);
   }

   /**
    * Stops the previously started performance logging
    */
   public void endLogInfo() {
      TslEntry tslEntry = DURATION_STACK.pop();
      endLogInfo(tslEntry);
   }

   private void endLogInfo(TslEntry tslEntry) {
      String timeConsumedInfo = "(duration=" + (System.currentTimeMillis() - tslEntry.start) + "ms)";
      LOG.info("Stop " + tslEntry.name + " " + timeConsumedInfo);
   }

   private record TslEntry(String name, long start) {
      // no-op
   }
}
