package com.aquabasilea.migrosapi.model.getcourse.response;

import com.aquabasilea.migrosapi.v1.model.book.response.CourseBookResult;

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
      if (this.code == CourseBookResult.COURSE_NOT_BOOKABLE_FULLY_BOOKED.getErrorCode()) {
         return CourseBookResult.COURSE_NOT_BOOKABLE_FULLY_BOOKED;
      }
      return CourseBookResult.COURSE_NOT_BOOKED;
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
//   Kurs ausgebucht:			{"bookingIdTac":0,"code":1,"message":"Technisches Problem 1507 (1507)"}
//   Kurs erfolgreich gebucht:	{"bookingIdTac":14891205,"code":0,"message":null}
}
