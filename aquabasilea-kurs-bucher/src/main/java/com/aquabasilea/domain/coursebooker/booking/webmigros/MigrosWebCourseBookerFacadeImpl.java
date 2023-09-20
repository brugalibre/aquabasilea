package com.aquabasilea.domain.coursebooker.booking.webmigros;

import com.aquabasilea.domain.course.model.Course;
import com.aquabasilea.domain.coursebooker.booking.facade.model.CourseCancelResult;
import com.aquabasilea.domain.coursebooker.states.booking.facade.AquabasileaCourseBookerFacade;
import com.aquabasilea.domain.coursebooker.states.booking.facade.CourseBookContainer;
import com.aquabasilea.web.bookcourse.AquabasileaWebCourseBooker;
import com.aquabasilea.web.bookcourse.impl.AquabasileaWebCourseBookerImpl;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseBookingEndResult;

import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

/**
 * A {@link AquabasileaCourseBookerFacade} implementation which relies on a selenium based implementation whic
 * clicks through the websites from migros
 */
public record MigrosWebCourseBookerFacadeImpl(String username, Supplier<char[]> userPassword,
                                              Supplier<Duration> duration2WaitUntilCourseBecomesBookable) implements AquabasileaCourseBookerFacade {
   @Override
   public CourseBookingEndResult selectAndBookCourse(CourseBookContainer courseBookContainer) {
      AquabasileaWebCourseBooker aquabasileaWebNavigator = AquabasileaWebCourseBookerImpl.createAndInitAquabasileaWebNavigator(username, userPassword.get(),
              courseBookContainer.bookingContext().dryRun(), duration2WaitUntilCourseBecomesBookable);
      return aquabasileaWebNavigator.selectAndBookCourse(courseBookContainer.courseBookDetails());
   }

   @Override
   public List<Course> getBookedCourses() {
      throw new IllegalStateException("Not implemented!");
   }

   @Override
   public CourseCancelResult cancelCourses(String bookingId) {
      throw new IllegalStateException("Not implemented!");
   }
}
