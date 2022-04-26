package com.aquabasilea.rest.service;

import com.aquabasilea.coursebooker.AquabasileaCourseBooker;
import com.aquabasilea.rest.model.CourseBookingState;
import com.aquabasilea.rest.model.CourseBookingStateDto;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AquabasileaCourseBookerService {

   private static final Logger LOG = LoggerFactory.getLogger(WeeklyCoursesService.class);
   private final AquabasileaCourseBooker aquabasileaCourseBooker;

   @Autowired
   public AquabasileaCourseBookerService(AquabasileaCourseBooker aquabasileaCourseBooker) {
      this.aquabasileaCourseBooker = aquabasileaCourseBooker;
   }

   public boolean isPaused() {
      return aquabasileaCourseBooker.isPaused();
   }

   public CourseBookingStateDto getCourseBookingStateDto() {
      CourseBookingState state = map2CourseBookingState();
      return new CourseBookingStateDto(aquabasileaCourseBooker.getInfoString4State(), state);
   }

   public void pauseOrResume() {
      aquabasileaCourseBooker.pauseOrResume();
      LOG.info(aquabasileaCourseBooker.isPaused() ? "Application is paused" : "Application is resumed");
   }

   @NotNull
   private CourseBookingState map2CourseBookingState() {
      CourseBookingState state = CourseBookingState.IDLE;
      if (aquabasileaCourseBooker.isPaused()) {
         state = CourseBookingState.PAUSED;
      } else if (aquabasileaCourseBooker.isBookingCourse() || aquabasileaCourseBooker.isBookingCourse()) {
         state = CourseBookingState.BOOKING;
      }
      return state;
   }
}
