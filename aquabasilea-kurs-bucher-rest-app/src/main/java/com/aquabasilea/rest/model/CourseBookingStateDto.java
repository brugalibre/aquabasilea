package com.aquabasilea.rest.model;

import com.aquabasilea.i18n.TextResources;

public class CourseBookingStateDto {
   private final String stateMsg;
   private final CourseBookingState state;
   private String pauseOrResumeButtonText;

   public CourseBookingStateDto(String stateMsg, CourseBookingState state) {
      this.stateMsg = stateMsg;
      this.state = state;
      if (state == CourseBookingState.PAUSED) {
         this.pauseOrResumeButtonText = TextResources.RESUME_APP;
      } else if (state == CourseBookingState.IDLE || state == CourseBookingState.BOOKING) {
         this.pauseOrResumeButtonText = TextResources.PAUSE_APP;
      }
   }

   public String getStateMsg() {
      return stateMsg;
   }

   public CourseBookingState getState() {
      return state;
   }

   public String getPauseOrResumeButtonText() {
      return pauseOrResumeButtonText;
   }
}
