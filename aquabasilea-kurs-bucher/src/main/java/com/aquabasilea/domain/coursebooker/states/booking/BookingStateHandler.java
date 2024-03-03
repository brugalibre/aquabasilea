package com.aquabasilea.domain.coursebooker.states.booking;

import com.aquabasilea.application.i18n.TextResources;
import com.aquabasilea.domain.course.model.Course;
import com.aquabasilea.domain.course.model.CourseComparator;
import com.aquabasilea.domain.course.model.WeeklyCourses;
import com.aquabasilea.domain.course.repository.WeeklyCoursesRepository;
import com.aquabasilea.domain.coursebooker.states.booking.facade.CourseBookerFacade;
import com.aquabasilea.domain.coursebooker.model.booking.CourseBookDetails;
import com.aquabasilea.domain.coursebooker.model.booking.result.CourseBookResult;
import com.aquabasilea.domain.coursebooker.model.booking.result.CourseBookingResultDetails;
import com.aquabasilea.domain.coursebooker.model.booking.result.CourseBookingResultDetailsImpl;
import com.aquabasilea.domain.coursebooker.model.state.CourseBookingState;
import com.aquabasilea.domain.coursebooker.model.booking.BookingContext;
import com.aquabasilea.domain.coursebooker.model.booking.CourseBookContainer;
import com.aquabasilea.domain.coursedef.model.CourseDef;
import com.aquabasilea.util.PlUtil;
import com.brugalibre.domain.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@link BookingStateHandler} handles the state of the actual booking
 * So either {@link CourseBookingState#BOOKING} or {@link CourseBookingState#BOOKING_DRY_RUN}
 */
public class BookingStateHandler {
   private static final Logger LOG = LoggerFactory.getLogger(BookingStateHandler.class);
   private final CourseBookerFacade courseBookerFacade;
   private final WeeklyCoursesRepository weeklyCoursesRepository;

   public BookingStateHandler(WeeklyCoursesRepository weeklyCoursesRepository,
                              CourseBookerFacade courseBookerFacade) {
      this.courseBookerFacade = courseBookerFacade;
      this.weeklyCoursesRepository = weeklyCoursesRepository;
   }

   /**
    * Does the actual booking or dry-run of the current Course but only, if there is a {@link CourseDef}
    * for the given course.
    *
    * @param userId             the id uf the {@link User} whose {@link Course}s are resumed
    * @param courseId           the id of the {@link Course} to book
    * @param courseBookingState the current {@link CourseBookingState}
    * @return a {@link CourseBookingResultDetails} with details about the booking
    */
   public CourseBookingResultDetails bookCourse(String userId, String courseId, CourseBookingState courseBookingState) {
      Course courseById = getCourseById(userId, courseId);
      return bookCourse(userId, courseById, courseBookingState);
   }

   private Course getCourseById(String userId, String courseId) {
      WeeklyCourses weeklyCourses = weeklyCoursesRepository.getByUserId(userId);
      return weeklyCourses.getCourseById(courseId);
   }

   /**
    * Does the actual booking or dry-run of the current Course but only, if there is a {@link CourseDef}
    * for the given course.
    *
    * @param userId        the id uf the {@link User} whose {@link Course}s are resumed
    * @param currentCourse the {@link Course} to book
    * @param state         the current {@link CourseBookingState}
    * @return a {@link CourseBookingResultDetails} with details about the booking
    */
   public CourseBookingResultDetails bookCourse(String userId, Course currentCourse, CourseBookingState state) {
      if (!currentCourse.getHasCourseDef()) {
         return handleCourseWithoutCourseDef(currentCourse);
      }
      LOG.info("About going to {} the course [{}]", state == CourseBookingState.BOOKING ? "book" : "dry-run",
              currentCourse.getCourseName());
      CourseBookContainer courseBookContainer = createCourseBookContainer(currentCourse, state);
      PlUtil.INSTANCE.startLogInfo("Course booker");
      CourseBookingResultDetails courseBookingEndResult = bookCourse(courseBookContainer);
      LOG.info("Course booking  is done. Result is '{}'", courseBookingEndResult);
      PlUtil.INSTANCE.endLogInfo();
      if (state == CourseBookingState.BOOKING) {
         resumeCoursesUntil(userId, currentCourse);
      }
      return courseBookingEndResult;
   }

   private CourseBookingResultDetails bookCourse(CourseBookContainer courseBookContainer) {
      try {
         return courseBookerFacade.bookCourse(courseBookContainer);
      } catch (Exception e) {
         String courseName = courseBookContainer.courseBookDetails().courseName();
         LOG.error("Error while booking course {}", courseName, e);
         return createErrorCourseBookingResultDetails(courseBookContainer.bookingContext(), courseName);
      }
   }

   private static CourseBookingResultDetailsImpl createErrorCourseBookingResultDetails(BookingContext bookingContext, String courseName) {
      CourseBookResult courseBookResult = bookingContext.dryRun() ? CourseBookResult.DRY_RUN_FAILED : CourseBookResult.NOT_BOOKED_UNEXPECTED_ERROR;
      return CourseBookingResultDetailsImpl.of(courseBookResult, courseName, TextResources.COURSE_BOOKING_FAILED.formatted(courseName));
   }

   /**
    * All paused Courses which take place before the given <code>currentCourse</code> are resumed.
    * The idea is to automatically resume all paused courses, as soon as they lie in the past regarding the given course
    *
    * @param userId        the id uf the {@link User} whose {@link Course}s are resumed
    * @param currentCourse the course which marks the next {@link Course} to book
    */
   public void resumeCoursesUntil(String userId, Course currentCourse) {
      LOG.info("Resumes previously paused curses");
      WeeklyCourses weeklyCourses = weeklyCoursesRepository.getByUserId(userId);
      List<Course> courses = new ArrayList<>(weeklyCourses.getCourses());
      courses.sort(new CourseComparator());
      for (Course course : courses) {
         course.setIsPaused(false);
         if (course.getId().equals(currentCourse.getId())) {
            break;
         }
      }
      weeklyCoursesRepository.save(weeklyCourses);
   }

   private static CourseBookingResultDetails handleCourseWithoutCourseDef(Course currentCourse) {
      LOG.warn("Course {} not booked, because there exist no real aquabasilea-course counterpart!", currentCourse.getCourseName());
      return CourseBookingResultDetailsImpl.of(CourseBookResult.BOOKING_SKIPPED, currentCourse.getCourseName(), null);
   }

   private CourseBookContainer createCourseBookContainer(Course currentCourse, CourseBookingState state) {
      CourseBookDetails courseBookDetails = new CourseBookDetails(currentCourse.getCourseName(), currentCourse.getCourseInstructor(),
              currentCourse.getCourseDate(), currentCourse.getCourseLocation());
      return new CourseBookContainer(courseBookDetails, new BookingContext(state == CourseBookingState.BOOKING_DRY_RUN));
   }
}
