package com.aquabasilea.migrosapi.model.book.api;

import java.time.Duration;
import java.util.function.Supplier;

public record MigrosBookContext(boolean dryRun, Supplier<Duration> duration2WaitUntilCourseBecomesBookable) {
   // no-op
}
