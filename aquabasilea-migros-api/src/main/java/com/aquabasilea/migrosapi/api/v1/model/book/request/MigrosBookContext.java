package com.aquabasilea.migrosapi.api.v1.model.book.request;

import java.time.Duration;
import java.util.function.Supplier;

public record MigrosBookContext(boolean dryRun, Supplier<Duration> duration2WaitUntilCourseBecomesBookable) {
   // no-op
}
