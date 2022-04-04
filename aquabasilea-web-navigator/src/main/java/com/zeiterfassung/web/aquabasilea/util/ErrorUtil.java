package com.zeiterfassung.web.aquabasilea.util;

import java.util.Arrays;

public class ErrorUtil {
   private ErrorUtil() {
      // private
   }

   public static String getErrorMsgWithException(Exception e) {
      String stacktrace = Arrays.stream(e.getStackTrace()).sequential()
              .map(StackTraceElement::toString)
              .reduce("", (a, b) -> a + b + "\n");
      return "Error during course booking:\n" +
              e.getMessage() + "\n" +
              stacktrace;
   }
}
