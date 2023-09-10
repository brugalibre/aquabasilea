package com.aquabasilea.migrosapi.v1.model.book.request;

import java.time.Duration;
import java.util.function.Supplier;

public record MigrosBookContext(boolean dryRun, Supplier<Duration> duration2WaitUntilCourseBecomesBookable) {
   // no-op
}
