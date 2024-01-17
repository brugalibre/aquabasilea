package com.aquabasilea.domain.coursebooker.model.booking.cancel;

public record CourseCancelResultDetails(CourseCancelResult courseCancelResult, String errorMsg) {
   public static CourseCancelResultDetails notCanceled() {
      return new CourseCancelResultDetails(CourseCancelResult.COURSE_NOT_CANCELED, null);
   }
}
