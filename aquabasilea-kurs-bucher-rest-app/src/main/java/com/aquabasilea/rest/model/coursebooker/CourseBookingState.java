package com.aquabasilea.rest.model.coursebooker;

public enum CourseBookingState {
   /**
    * Application is paused until resumed. Nothing happens in between
    */
   PAUSED,

   /**
    * Application is idle and awaits the next course to book
    */
   IDLE,

   /**
    * Application is booking a course
    */
   BOOKING
}
