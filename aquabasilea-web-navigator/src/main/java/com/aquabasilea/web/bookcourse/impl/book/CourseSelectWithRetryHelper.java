package com.aquabasilea.web.bookcourse.impl.book;

import com.aquabasilea.web.bookcourse.AquabasileaWebCourseBooker;
import com.aquabasilea.web.bookcourse.impl.AquabasileaWebCourseBookerImpl;
import com.aquabasilea.web.bookcourse.impl.select.CourseSelectHelper;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseClickedResult;
import com.aquabasilea.web.bookcourse.model.CourseBookDetails;
import com.aquabasilea.web.constant.AquabasileaWebConst;
import com.aquabasilea.web.error.ErrorHandler;
import com.aquabasilea.web.filtercourse.CourseFilterHelper;
import com.zeiterfassung.web.common.navigate.util.WebNavigateUtil;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.function.Supplier;

import static com.aquabasilea.web.constant.AquabasileaWebConst.PAGE_REFRESH_DURATION;

/**
 * After the {@link CourseFilterHelper} has filtered the courses, the {@link CourseSelectWithRetryHelper}
 * uses the {@link CourseSelectHelper} in order to select and book a course once. A course becomes first bookable 24h before it's due date.
 * The {@link AquabasileaWebCourseBooker} starts a little earlier when doing the final booking
 * <p>
 * If the {@link CourseClickedResult} of this attempt is {@link CourseClickedResult#COURSE_NOT_BOOKED_RETRY} then this {@link CourseSelectWithRetryHelper}
 * retry the selecting of the course as often as it takes until the course becomes either bookable or fully booked.
 * For this the <code>duration2WaitUntilCourseBecomesBookable</code> is used to determine the remaining time to wait until the course should be bookable.
 * Before each selecting, the filter are reapplied, since they may get lost during a refresh of the page
 * <p>
 * Additionally, between each retry it waits the {@link AquabasileaWebConst#PAGE_REFRESH_DURATION} time until the course-page is refreshed and ready to retry
 */
public class CourseSelectWithRetryHelper {
   private static final Logger LOG = LoggerFactory.getLogger(AquabasileaWebCourseBookerImpl.class);
   private final Supplier<Duration> duration2WaitUntilCourseBecomesBookable;
   private final Duration pageRefreshDuration;
   private final CourseSelectHelper courseSelectHelper;
   private final CourseFilterHelper courseFilterHelper;
   private final Runnable pageRefresher;

   public CourseSelectWithRetryHelper(CourseSelectHelper courseSelectHelper, CourseFilterHelper courseFilterHelper,
                                      Runnable pageRefresher, Supplier<Duration> duration2WaitUntilCourseBecomesBookable) {
      this.duration2WaitUntilCourseBecomesBookable = duration2WaitUntilCourseBecomesBookable;
      this.pageRefreshDuration = PAGE_REFRESH_DURATION;
      this.courseSelectHelper = courseSelectHelper;
      this.courseFilterHelper = courseFilterHelper;
      this.pageRefresher = pageRefresher;
   }

   /**
    * Tries to select and book the course, defined by the given {@link CourseBookDetails}. This is done by the {@link CourseSelectHelper}
    * If the returned {@link CourseClickedResult} is {@link CourseClickedResult#COURSE_NOT_BOOKED_RETRY} then this step is repeated until
    * we succeed or fail.
    *
    * @param courseBookDetails the {@link CourseBookDetails}
    * @param errorHandler      the {@link ErrorHandler} to handle missing {@link WebElement}
    * @return a {@link CourseClickedResult} describing the outcome of the selecting procedure
    * @see CourseSelectHelper
    */
   public CourseClickedResult selectAndBookCourseWithRetry(CourseBookDetails courseBookDetails, ErrorHandler errorHandler) {
      CourseClickedResult courseClickedResult = courseSelectHelper.selectCourseAndBook(courseBookDetails.courseName(), errorHandler);
      return filterAndSelectAgainIfNecessary(courseBookDetails, errorHandler, courseClickedResult);
   }

   private CourseClickedResult filterAndSelectAgainIfNecessary(CourseBookDetails courseBookDetails, ErrorHandler errorHandler, CourseClickedResult courseClickedResult) {
      while (courseClickedResult == CourseClickedResult.COURSE_NOT_BOOKED_RETRY) {
         long millis2Wait = duration2WaitUntilCourseBecomesBookable.get().toMillis();
         LOG.info("Course '{}' not yet available, {}ms left until course becomes bookable, Refresh page and do retry..", courseBookDetails.courseName(), millis2Wait);
         waitAndRefreshCoursePage(millis2Wait);
         LOG.info("Page refreshed, try to filter and select course again");
         courseFilterHelper.applyCriteriaFilter(courseBookDetails, errorHandler);
         courseClickedResult = courseSelectHelper.selectCourseAndBook(courseBookDetails.courseName(), errorHandler);
      }
      return courseClickedResult;
   }

   private void waitAndRefreshCoursePage(long millis2Wait) {
      WebNavigateUtil.waitForMilliseconds((int) (millis2Wait - pageRefreshDuration.toMillis()));
      this.pageRefresher.run();
   }
}
