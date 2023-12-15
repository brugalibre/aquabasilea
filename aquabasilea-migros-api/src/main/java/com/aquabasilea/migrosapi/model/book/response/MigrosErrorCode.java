package com.aquabasilea.migrosapi.model.book.response;

/**
 * The {@link MigrosErrorCode} defines the internal error codes used by the migros-api
 */
public enum MigrosErrorCode {

   /**
    * An unknown technical error has occurred.
    */
   UNKNOWN_TECHNICAL_ERROR(1506, "Technisches Problem 1506"),

   /**
    * The course is full and therefore no other participants are allowed
    */
   COURSE_IS_FULLY_BOOKED(1507, "Technisches Problem 1507"),

   /**
    * The course is already booked by the user
    */
   COURSE_IS_ALREADY_BOOKED(2300, "Technisches Problem 2300");

   static final int RESPONSE_CODE_ERROR = 1;

   private final int errorCode;
   private final String technicalMigrosErrorMsg;

   MigrosErrorCode(int errorCode, String technicalMigrosErrorMsg) {
      this.errorCode = errorCode;
      this.technicalMigrosErrorMsg = technicalMigrosErrorMsg;
   }

   public int getErrorCode() {
      return errorCode;
   }

   public String getTechnicalMigrosErrorMsg() {
      return technicalMigrosErrorMsg;
   }
}
