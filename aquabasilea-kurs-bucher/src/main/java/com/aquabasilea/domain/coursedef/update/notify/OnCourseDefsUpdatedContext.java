package com.aquabasilea.domain.coursedef.update.notify;

import com.aquabasilea.domain.coursedef.model.CourseDef;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public record OnCourseDefsUpdatedContext(String userId, List<CourseDef> updatedCourseDefs,
                                         LocalDateTime dateWhenUpdateStarted, Duration durationUntilNextUpdate) {
}
