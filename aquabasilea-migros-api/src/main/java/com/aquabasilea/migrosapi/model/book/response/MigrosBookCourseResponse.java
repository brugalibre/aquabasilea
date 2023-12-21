package com.aquabasilea.migrosapi.model.book.response;

import com.aquabasilea.migrosapi.api.v1.model.book.response.CourseBookResult;

import static com.aquabasilea.migrosapi.model.book.response.MigrosErrorCode.RESPONSE_CODE_ERROR;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class MigrosBookCourseResponse {
   private int bookingIdTac;
   private int code;
   private String message;

   public CourseBookResult getCourseBookResult() {
      return isCourseSuccessfullyBooked() ?
              CourseBookResult.COURSE_BOOKED
              : getCourseNotBookedCode();
   }

   private CourseBookResult getCourseNotBookedCode() {
      if (this.code == RESPONSE_CODE_ERROR && message != null) {
         if (message.contains(MigrosErrorCode.COURSE_IS_ALREADY_BOOKED.getTechnicalMigrosErrorMsg())) {
            return CourseBookResult.COURSE_NOT_BOOKABLE_ALREADY_BOOKED;
         } else if (message.contains(MigrosErrorCode.COURSE_IS_FULLY_BOOKED.getTechnicalMigrosErrorMsg())) {
            return CourseBookResult.COURSE_NOT_BOOKABLE_FULLY_BOOKED;
         } else if (message.contains(MigrosErrorCode.UNKNOWN_TECHNICAL_ERROR.getTechnicalMigrosErrorMsg())) {
            return CourseBookResult.COURSE_NOT_BOOKABLE_TECHNICAL_ERROR;
         }
      }
      return CourseBookResult.COURSE_NOT_BOOKED_UNEXPECTED_ERROR;
   }

   public void setBookingIdTac(int bookingIdTac) {
      this.bookingIdTac = bookingIdTac;
   }

   public void setCode(int code) {
      this.code = code;
   }

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      if (nonNull(message)) {
         this.message = message;
      }
   }

   private boolean isCourseSuccessfullyBooked() {
      return bookingIdTac > 0 && isNull(message)
              && code == 0;
   }
}
