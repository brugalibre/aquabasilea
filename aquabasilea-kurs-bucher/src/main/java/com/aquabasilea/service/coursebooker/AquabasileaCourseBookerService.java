package com.aquabasilea.service.coursebooker;

import com.aquabasilea.domain.course.model.Course;
import com.aquabasilea.domain.coursebooker.AquabasileaCourseBooker;
import com.aquabasilea.domain.coursebooker.AquabasileaCourseBookerHolder;
import com.aquabasilea.domain.coursebooker.model.state.CourseBookingStateOverview;
import com.aquabasilea.domain.coursebooker.states.CourseBookingState;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseBookingEndResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.aquabasilea.domain.coursebooker.states.CourseBookingState.BOOKING_DRY_RUN;

@Service
public class AquabasileaCourseBookerService {

   private static final Logger LOG = LoggerFactory.getLogger(AquabasileaCourseBookerService.class);
   private final AquabasileaCourseBookerHolder aquabasileaCourseBookerHolder;

   @Autowired
   public AquabasileaCourseBookerService(AquabasileaCourseBookerHolder aquabasileaCourseBookerHolder) {
      this.aquabasileaCourseBookerHolder = aquabasileaCourseBookerHolder;
   }

   public boolean isPaused(String userId) {
      AquabasileaCourseBooker aquabasileaCourseBooker = getAquabasileaCourseBooker4CurrentUser(userId);
      return aquabasileaCourseBooker.isPaused();
   }

   public CourseBookingStateOverview getCourseBookingState(String userId) {
      AquabasileaCourseBooker aquabasileaCourseBooker = getAquabasileaCourseBooker4CurrentUser(userId);
      CourseBookingState courseBookingState = map2CourseBookingState(aquabasileaCourseBooker);
      return new CourseBookingStateOverview(courseBookingState, aquabasileaCourseBooker.getInfoString4State());
   }

   public void pauseOrResume(String userId) {
      AquabasileaCourseBooker aquabasileaCourseBooker = getAquabasileaCourseBooker4CurrentUser(userId);
      aquabasileaCourseBooker.pauseOrResume();
      LOG.info(aquabasileaCourseBooker.isPaused() ? "Application is paused" : "Application is resumed");
   }

   /**
    * Does a dry-run booking of the current Course. Additionally, all consumers are notified about the result
    *
    * @param courseId the id of the {@link Course} to book
    * @return a {@link CourseBookingEndResult} which contains details about the result
    */
   public CourseBookingEndResult bookCourseDryRun(String userId, String courseId) {
      LOG.info("Start dry run for course {}", courseId);
      AquabasileaCourseBooker aquabasileaCourseBooker = getAquabasileaCourseBooker4CurrentUser(userId);
      return aquabasileaCourseBooker.bookCourse(BOOKING_DRY_RUN, courseId, true);
   }
   private CourseBookingState map2CourseBookingState(AquabasileaCourseBooker aquabasileaCourseBooker) {
      CourseBookingState state = CourseBookingState.IDLE_BEFORE_BOOKING;
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
