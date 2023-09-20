package com.aquabasilea.service.coursebooker;

import com.aquabasilea.domain.course.model.Course;
import com.aquabasilea.domain.coursebooker.AquabasileaCourseBooker;
import com.aquabasilea.domain.coursebooker.AquabasileaCourseBookerHolder;
import com.aquabasilea.domain.coursebooker.booking.facade.model.CourseCancelResult;
import com.aquabasilea.domain.coursebooker.model.state.CourseBookingStateOverview;
import com.aquabasilea.domain.coursebooker.states.CourseBookingState;
import com.aquabasilea.service.coursebooker.bookedcourses.BookedCourseHelper;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseBookingEndResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.aquabasilea.domain.coursebooker.states.CourseBookingState.BOOKING_DRY_RUN;

@Service
public class AquabasileaCourseBookerService {

   private static final Logger LOG = LoggerFactory.getLogger(AquabasileaCourseBookerService.class);
   private final AquabasileaCourseBookerHolder aquabasileaCourseBookerHolder;
   private final BookedCourseHelper bookedCourseHelper;

   @Autowired
   public AquabasileaCourseBookerService(AquabasileaCourseBookerHolder aquabasileaCourseBookerHolder) {
      this.aquabasileaCourseBookerHolder = aquabasileaCourseBookerHolder;
      this.bookedCourseHelper = new BookedCourseHelper(aquabasileaCourseBookerHolder);
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
    * Returns for the user with the given id all booked {@link Course}s
    *
    * @param userId the id of the user
    * @return for the user with the given id all booked {@link Course}s
    */
   public List<Course> getBookedCourses(String userId) {
      return bookedCourseHelper.getBookedCourses(userId);
   }

   /**
    * Cancels the course which is associated with the given booking id and returns a {@link CourseCancelResult}
    *
    * @param userId    the id of the user for who the given booking is going to be canceled
    * @param bookingId the id of the booking which should be canceled
    * @return a {@link CourseCancelResult}
    */
   public CourseCancelResult cancelBookedCourse(String userId, String bookingId) {
      AquabasileaCourseBooker aquabasileaCourseBooker = getAquabasileaCourseBooker4CurrentUser(userId);
      return aquabasileaCourseBooker.cancelBookedCourse(bookingId);
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
