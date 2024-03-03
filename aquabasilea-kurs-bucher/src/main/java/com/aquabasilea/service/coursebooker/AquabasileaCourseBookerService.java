package com.aquabasilea.service.coursebooker;

import com.aquabasilea.application.config.logging.MdcConst;
import com.aquabasilea.domain.course.model.Course;
import com.aquabasilea.domain.coursebooker.AquabasileaCourseBooker;
import com.aquabasilea.domain.coursebooker.AquabasileaCourseBookerHolder;
import com.aquabasilea.domain.coursebooker.model.booking.cancel.CourseCancelResult;
import com.aquabasilea.domain.coursebooker.model.booking.cancel.CourseCancelResultDetails;
import com.aquabasilea.domain.coursebooker.model.booking.result.CourseBookingResultDetails;
import com.aquabasilea.domain.coursebooker.model.state.CourseBookingStateOverview;
import com.aquabasilea.domain.coursebooker.model.state.CourseBookingState;
import com.aquabasilea.service.coursebooker.bookedcourses.BookedCourseHelper;
import com.brugalibre.domain.user.model.User;
import com.brugalibre.domain.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.aquabasilea.domain.coursebooker.model.state.CourseBookingState.BOOKING_DRY_RUN;

@Service
public class AquabasileaCourseBookerService {

   private static final Logger LOG = LoggerFactory.getLogger(AquabasileaCourseBookerService.class);
   private final AquabasileaCourseBookerHolder aquabasileaCourseBookerHolder;
   private final BookedCourseHelper bookedCourseHelper;
   private final UserRepository userRepository;

   @Autowired
   public AquabasileaCourseBookerService(AquabasileaCourseBookerHolder aquabasileaCourseBookerHolder,
                                         UserRepository userRepository) {
      this.aquabasileaCourseBookerHolder = aquabasileaCourseBookerHolder;
      this.bookedCourseHelper = new BookedCourseHelper(aquabasileaCourseBookerHolder);
      this.userRepository = userRepository;
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

   /**
    * Pauses or resumes the {@link AquabasileaCourseBooker} which belongs to the given user-id.
    * If currently all courses are paused, then all courses are resumed as well.
    * <b>Note:</b> this only takes effect if <b>all</b> courses are paused. So if there exists one un-paused course,
    * only the {@link AquabasileaCourseBooker} is resumed
    *
    * @see Course#getIsPaused()
    * @param userId the id of the user
    */
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
    * @return a {@link CourseCancelResultDetails}
    */
   public CourseCancelResultDetails cancelBookedCourse(String userId, String bookingId) {
      AquabasileaCourseBooker aquabasileaCourseBooker = getAquabasileaCourseBooker4CurrentUser(userId);
      return aquabasileaCourseBooker.cancelBookedCourse(bookingId);
   }

   /**
    * Cancels the course for the user associated with the given phone-nr. If this user has only one booked course,
    * that one is canceled. If there are more than one, than the one that matches the given course-name is canceled
    *
    * @param phoneNr    the phone-Nr of the user for whom the given booking is going to be canceled
    * @param courseName the optional name of the course to cancel. Only required if there are more than one booked courses
    * @return a {@link CourseCancelResult}
    */
   public CourseCancelResultDetails cancelCourse4PhoneNr(String phoneNr, String courseName) {
      LOG.info("Cancel course '{}' for user with phone-nr '{}'", courseName, phoneNr);
      Optional<User> optUserByPhoneNr = userRepository.findByPhoneNr(phoneNr);
      if (optUserByPhoneNr.isPresent()) {
         User user = optUserByPhoneNr.get();
         try {
            MDC.put(MdcConst.USER_ID, user.id());
            return cancelCourse4User(courseName, user);
         } finally {
            MDC.remove(MdcConst.USER_ID);
         }
      } else {
         LOG.warn("No user found for phone-nr '{}'", phoneNr);
         return CourseCancelResultDetails.notCanceled();
      }
   }

   private CourseCancelResultDetails cancelCourse4User(String courseName, User user) {
      List<Course> bookedCourses = getBookedCourses(user.id());
      LOG.info("The booked courses {} are found to cancel", bookedCourses);
      if (bookedCourses.size() == 1) {
         Course course = bookedCourses.get(0);
         return cancelBookedCourse(user.id(), course.getBookingIdTac());
      } else {
         return bookedCourses.stream()
                 .filter(course -> course.getCourseName().equals(courseName))
                 .findFirst()
                 .map(course -> cancelBookedCourse(user.id(), course.getBookingIdTac()))
                 .orElse(CourseCancelResultDetails.notCanceled());
      }
   }

   /**
    * Does a dry-run booking of the current Course. Additionally, all consumers are notified about the result
    *
    * @param dryRunInfo@return a {@link CourseBookingResultDetails} which contains details about the result
    */
   public CourseBookingResultDetails bookCourseDryRun(DryRunInfo dryRunInfo) {
      LOG.info("Start dry run for course {}", dryRunInfo.courseId());
      AquabasileaCourseBooker aquabasileaCourseBooker = getAquabasileaCourseBooker4CurrentUser(dryRunInfo.userId());
      return aquabasileaCourseBooker.bookCourse(BOOKING_DRY_RUN, dryRunInfo.courseId(), dryRunInfo.sendNotification());
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
