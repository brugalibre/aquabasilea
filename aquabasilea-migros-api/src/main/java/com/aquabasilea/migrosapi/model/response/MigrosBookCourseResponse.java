package com.aquabasilea.migrosapi.model.response;

import com.aquabasilea.migrosapi.model.response.common.CommonHttpResponse;

import static java.util.Objects.isNull;

public class MigrosBookCourseResponse extends CommonHttpResponse {
   private int bookingIdTac;
   private int code;
   private String message;

   public int getBookingIdTac() {
      return bookingIdTac;
   }

   public void setBookingIdTac(int bookingIdTac) {
      this.bookingIdTac = bookingIdTac;
   }

   public int getCode() {
      return code;
   }

   public void setCode(int code) {
      this.code = code;
   }

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   public boolean isCourseSuccessfullyBooked() {
      return bookingIdTac > 0 && isNull(message)
              && code == 0;
   }
   //{"bookingIdTac":14891205,"code":0,"message":null}
//   {"bookingIdTac":0,"code":1,"message":"Technisches Problem 2300 (2300)"}
}
