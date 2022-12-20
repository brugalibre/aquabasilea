package com.aquabasilea.rest.service;

import com.aquabasilea.coursebooker.AquabasileaCourseBooker;
import com.aquabasilea.coursebooker.AquabasileaCourseBookerHolder;
import com.aquabasilea.rest.model.CourseBookingState;
import com.aquabasilea.rest.model.CourseBookingStateDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AquabasileaCourseBookerService {

   private static final Logger LOG = LoggerFactory.getLogger(WeeklyCoursesRestService.class);
   private final AquabasileaCourseBookerHolder aquabasileaCourseBookerHolder;

   @Autowired
   public AquabasileaCourseBookerService(AquabasileaCourseBookerHolder aquabasileaCourseBookerHolder) {
      this.aquabasileaCourseBookerHolder = aquabasileaCourseBookerHolder;
   }

   public boolean isPaused(String userId) {
      AquabasileaCourseBooker aquabasileaCourseBooker = getAquabasileaCourseBooker4CurrentUser(userId);
      return aquabasileaCourseBooker.isPaused();
   }

   public CourseBookingStateDto getCourseBookingStateDto(String userId) {
      AquabasileaCourseBooker aquabasileaCourseBooker = getAquabasileaCourseBooker4CurrentUser(userId);
      CourseBookingState state = map2CourseBookingState(aquabasileaCourseBooker);
      return new CourseBookingStateDto(aquabasileaCourseBooker.getInfoString4State(), state);
   }

   public void pauseOrResume(String userId) {
      AquabasileaCourseBooker aquabasileaCourseBooker = getAquabasileaCourseBooker4CurrentUser(userId);
      aquabasileaCourseBooker.pauseOrResume();
      LOG.info(aquabasileaCourseBooker.isPaused() ? "Application is paused" : "Application is resumed");
   }

   private CourseBookingState map2CourseBookingState(AquabasileaCourseBooker aquabasileaCourseBooker) {
      CourseBookingState state = CourseBookingState.IDLE;
      if (aquabasileaCourseBooker.isPaused()) {
         state = CourseBookingState.PAUSED;
      } else if (aquabasileaCourseBooker.isBookingCourse() || aquabasileaCourseBooker.isBookingCourseDryRun()) {
         state = CourseBookingState.BOOKING;
      }
      return state;
   }

   private AquabasileaCourseBooker getAquabasileaCourseBooker4CurrentUser(String userId) {
      return aquabasileaCourseBookerHolder.getForUserId(userId);
   }
}
