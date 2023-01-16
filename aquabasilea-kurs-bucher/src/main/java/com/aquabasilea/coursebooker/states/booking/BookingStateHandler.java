package com.aquabasilea.coursebooker.states.booking;

import com.aquabasilea.coursebooker.model.course.weeklycourses.Course;
import com.aquabasilea.coursebooker.model.course.weeklycourses.CourseComparator;
import com.aquabasilea.coursebooker.model.course.weeklycourses.WeeklyCourses;
import com.aquabasilea.coursebooker.model.course.weeklycourses.repository.WeeklyCoursesRepository;
import com.aquabasilea.coursebooker.states.CourseBookingState;
import com.aquabasilea.coursedef.model.CourseDef;
import com.aquabasilea.util.PlUtil;
import com.aquabasilea.web.bookcourse.AquabasileaWebCourseBooker;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseBookingEndResult;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseBookingEndResult.CourseBookingEndResultBuilder;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseClickedResult;
import com.aquabasilea.web.bookcourse.model.CourseBookDetails;
import com.brugalibre.domain.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.aquabasilea.coursebooker.states.CourseBookingState.BOOKING;

/**
 * The {@link BookingStateHandler} handles the state of the actual booking
 * So either {@link CourseBookingState#BOOKING} or {@link CourseBookingState#BOOKING_DRY_RUN}
 */
public class BookingStateHandler {
   private static final Logger LOG = LoggerFactory.getLogger(BookingStateHandler.class);
   private final Supplier<AquabasileaWebCourseBooker> aquabasileaWebCourseBookerSupp;
   private final WeeklyCoursesRepository weeklyCoursesRepository;

   public BookingStateHandler(WeeklyCoursesRepository weeklyCoursesRepository, Supplier<AquabasileaWebCourseBooker> aquabasileaWebCourseBookerSupp) {
      this.aquabasileaWebCourseBookerSupp = aquabasileaWebCourseBookerSupp;
      this.weeklyCoursesRepository = weeklyCoursesRepository;
   }

   /**
    * Does the actual booking or dry-run of the current Course but only, if there is a {@link CourseDef}
    * for the given course.
    *
    * @param userId        the id uf the {@link User} whose {@link Course}s are resumed
    * @param currentCourse the {@link Course} to book
    * @param state         the current {@link CourseBookingState}
    * @return a {@link CourseBookingEndResult} with details about the booking
    */
   public CourseBookingEndResult bookCourse(String userId, Course currentCourse, CourseBookingState state) {
      if (!currentCourse.getHasCourseDef()) {
         return handleCourseWithoutCourseDef(userId, currentCourse);
      }
      LOG.info("About going to {} the course [{}] for user [{}]", state == BOOKING ? "book" : "dry-run the booking",
              currentCourse.getCourseName(), userId);
      CourseBookDetails courseBookDetails = createCourseBookDetails(currentCourse);
      PlUtil.INSTANCE.startLogInfo("Course booker");
      CourseBookingEndResult courseBookingEndResult = aquabasileaWebCourseBookerSupp.get().selectAndBookCourse(courseBookDetails);
      LOG.info("Course booking for user [{}] is done. Result is {}", userId, courseBookingEndResult);
      PlUtil.INSTANCE.endLogInfo();
      resumeCoursesUntil(userId, currentCourse);
      return courseBookingEndResult;
   }

   /**
    * All paused Courses which take place before the given <code>currentCourse</code> are resumed.
    * The idea is to automatically resume all paused courses, as soon as they lie in the past regarding the given course
    *
    * @param userId        the id uf the {@link User} whose {@link Course}s are resumed
    * @param currentCourse the course which marks the next {@link Course} to book
    */
   public void resumeCoursesUntil(String userId, Course currentCourse) {
      LOG.info("Resumes previously paused curses for user [{}]", userId);
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

   private static CourseBookingEndResult handleCourseWithoutCourseDef(String userId, Course currentCourse) {
      LOG.warn("Course {} not booked for user [{}], because there exist no real aquabasilea-course counterpart!", userId,
              currentCourse.getCourseName());
      return CourseBookingEndResultBuilder.builder()
              .withCourseClickedResult(CourseClickedResult.COURSE_BOOKING_SKIPPED)
              .withCourseName(currentCourse.getCourseName())
              .build();
   }

   private static CourseBookDetails createCourseBookDetails(Course currentCourse) {
      return new CourseBookDetails(currentCourse.getCourseName(), currentCourse.getCourseInstructor(),
              currentCourse.getCourseDate(), currentCourse.getCourseLocation().getCourseLocationName());
   }
}
