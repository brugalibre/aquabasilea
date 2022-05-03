package com.aquabasilea.coursebooker.states.booking;

import com.aquabasilea.course.Course;
import com.aquabasilea.course.CourseComparator;
import com.aquabasilea.course.WeeklyCourses;
import com.aquabasilea.course.repository.WeeklyCoursesRepository;
import com.aquabasilea.course.repository.yaml.impl.YamlWeeklyCoursesRepositoryImpl;
import com.aquabasilea.coursebooker.states.CourseBookingState;
import com.aquabasilea.util.DateUtil;
import com.aquabasilea.web.navigate.AquabasileaWebNavigator;
import com.aquabasilea.web.selectcourse.result.CourseBookingEndResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

import static com.aquabasilea.coursebooker.states.CourseBookingState.BOOKING;

/**
 * The {@link BookingStateHandler} handles the state of the actual booking
 * So either {@link CourseBookingState#BOOKING} or {@link CourseBookingState#BOOKING_DRY_RUN}
 */
public class BookingStateHandler {
   private static final Logger LOG = LoggerFactory.getLogger(BookingStateHandler.class);
   private final WeeklyCoursesRepository weeklyCoursesRepository;

   private Supplier<AquabasileaWebNavigator> aquabasileaWebNavigatorSupplier;

   public BookingStateHandler(String weeklyCoursesYmlFile, Supplier<AquabasileaWebNavigator> aquabasileaWebNavigatorSupplier) {
      this.aquabasileaWebNavigatorSupplier = aquabasileaWebNavigatorSupplier;
      this.weeklyCoursesRepository = new YamlWeeklyCoursesRepositoryImpl(weeklyCoursesYmlFile);
   }

   /**
    * Does the actual booking or dry-run of the current Course
    *
    * @param currentCourse the {@link Course} to book
    * @param state         the current {@link CourseBookingState}
    * @return a {@link CourseBookingEndResult} with details about the booking
    */
   public CourseBookingEndResult bookCourse(Course currentCourse, CourseBookingState state) {
      LOG.info("About going to {} the course '{}' at {}", state == BOOKING ? "book" : "dry-run the booking",
              currentCourse.getCourseName(), DateUtil.toStringWithSeconds(LocalDateTime.now(), Locale.GERMAN));
      DayOfWeek dayOfWeek = DateUtil.getDayOfWeekFromInput(currentCourse.getDayOfWeek(), Locale.GERMAN);
      CourseBookingEndResult courseBookingEndResult = aquabasileaWebNavigatorSupplier.get().selectAndBookCourse(currentCourse.getCourseName(), dayOfWeek);
      LOG.info("Course booking done. Result is {}", courseBookingEndResult);
      resumeCoursesUntil(currentCourse);
      return courseBookingEndResult;
   }

   /**
    * All paused Courses which take place before the given <code>currentCourse</code> are resumed.
    * The idea is to automatically resume all paused courses, as soon as they lie in the past regarding the given course
    *
    * @param currentCourse the course which marks the next {@link Course} to book
    */
   public void resumeCoursesUntil(Course currentCourse) {
      LOG.info("Resumes previously paused curses");
      WeeklyCourses weeklyCourses = weeklyCoursesRepository.findFirstWeeklyCourses();
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
}
