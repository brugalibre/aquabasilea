package com.aquabasilea.migrosapi.model.book.api;

import java.time.Duration;
import java.util.function.Supplier;

public record MigrosApiBookCourseRequest(String courseName, String weekDay, String centerId,
                                         MigrosBookContext migrosBookContext) {

   public static MigrosApiBookCourseRequest of(String courseName, String weekDay, String centerId, Supplier<Duration> duration2WaitUntilCourseBecomesBookable) {
      return new MigrosApiBookCourseRequest(courseName, weekDay, centerId, new MigrosBookContext(false, duration2WaitUntilCourseBecomesBookable));
   }
}
