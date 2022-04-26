package com.aquabasilea.rest.model;

public class CourseBookingStateDto {
   private String stateMsg;
   private CourseBookingState state;

   public CourseBookingStateDto(String stateMsg, CourseBookingState state) {
      this.stateMsg = stateMsg;
      this.state = state;
   }

   public String getStateMsg() {
      return stateMsg;
   }

   public CourseBookingState getState() {
      return state;
   }
}
