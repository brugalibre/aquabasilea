package com.aquabasilea.migrosapi.api.v1.model.book.response;

public enum CourseCancelResult {

   /**
    * Course successfully canceled
    */
   COURSE_CANCELED(0),

   /**
    * Cancel of the course failed
    */
   COURSE_CANCEL_FAILED(1);

   private final int errorCode;

   CourseCancelResult(int errorCode) {
      this.errorCode = errorCode;
   }

   public int getErrorCode() {
      return errorCode;
   }
}
