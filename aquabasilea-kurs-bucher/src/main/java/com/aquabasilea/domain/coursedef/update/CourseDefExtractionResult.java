package com.aquabasilea.domain.coursedef.update;

import com.aquabasilea.domain.coursedef.model.CourseDef;

import java.util.List;

public record CourseDefExtractionResult(List<CourseDef> courseDefs, boolean successful) {
   public static CourseDefExtractionResult empty() {
      return new CourseDefExtractionResult(List.of(), false);
   }
}
