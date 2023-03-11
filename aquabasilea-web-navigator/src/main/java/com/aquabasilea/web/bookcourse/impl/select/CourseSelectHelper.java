package com.aquabasilea.web.bookcourse.impl.select;

import com.aquabasilea.web.bookcourse.impl.book.CourseBookerHelper;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseClickedResult;
import com.aquabasilea.web.bookcourse.model.CourseBookDetails;
import com.aquabasilea.web.constant.AquabasileaWebConst;
import com.aquabasilea.web.error.ErrorHandler;
import com.aquabasilea.web.extractcourses.impl.AquabasileaCourseExtractorHelper;
import com.aquabasilea.web.extractcourses.model.AquabasileaCourse;
import com.aquabasilea.web.navigate.AquabasileaNavigatorHelper;
import com.zeiterfassung.web.common.navigate.util.WebNavigateUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.aquabasilea.web.constant.AquabasileaWebConst.*;
import static com.zeiterfassung.web.common.constant.BaseWebConst.HTML_BUTTON_TYPE;
import static com.zeiterfassung.web.common.constant.BaseWebConst.HTML_DIV_TYPE;
import static java.util.Objects.nonNull;

/**
 * The {@link CourseSelectHelper} does the actual selecting and booking of a course.
 * This helper assumes, that the courses to select were filtered in a previous step. It then gets all course-buttons from the web-element
 * 'course-table' ({@link AquabasileaWebConst#WEB_ELEMENT_COURSE_RESULTS_CONTENT_ATTR_VALUE}). If is exactly one result, this
 * result is clicked. However, if not an error is written in the {@link ErrorHandler}.
 * <p>
 * The click opens a detail dialog for the desired course. This course is then booked, if there is a booking-button.
 * If there is no booking button but a login button, then a login is done and then the course is booked.
 * <p>
 * If there is neither a login button nor a booking button, the returned {@link CourseClickedResult} depends on the current booking run.
 * If it's a dry run, then {@link CourseClickedResult#COURSE_BOOKING_ABORTED} is returned.
 * For a non-dry run the returned result depends on the time which remains, until the course becomes bookable. If this time
 * is greater than zero, {@link CourseClickedResult#COURSE_NOT_BOOKED_RETRY} is returned, indicating that this course is not yet bookable
 * and the selecting should be retried later.
 * If the remaining time is zero or below, then {@link CourseClickedResult#COURSE_NOT_BOOKABLE} is returned.
 */
public class CourseSelectHelper {

   private static final Logger LOG = LoggerFactory.getLogger(CourseSelectHelper.class);

   /**
    * This is the additional time we wait after the course became bookable
    */
   private final Duration durationToWaitAdditionalUntilACourseBecomesBookable;
   private final Duration waitForCourseTableToAppear;
   private final Duration waitForBookDialogToAppear;
   private final Duration waitForCourseContentToAppear;

   private final Supplier<Duration> duration2WaitUntilCourseBecomesBookable;
   private final CourseBookerHelper courseBookerHelper;
   private final AquabasileaNavigatorHelper aquabasileaNavigatorHelper;
   private final BookingAndCloseButtonMissingCallbackHandler missingBookingAndCloseButtonCallbackHandler;

   public CourseSelectHelper(CourseBookerHelper courseBookerHelper, AquabasileaNavigatorHelper aquabasileaNavigatorHelper,
                             Supplier<Duration> duration2WaitUntilCourseBecomesBookable, boolean dryRun,
                             Duration waitForCourseTableToAppear, Duration waitForBookDialogToAppear) {
      this.aquabasileaNavigatorHelper = aquabasileaNavigatorHelper;
      // It may happen, that the AquabasileaWebNavigator was started slightly to early and has to wait, until the booking button becomes available
      this.duration2WaitUntilCourseBecomesBookable = duration2WaitUntilCourseBecomesBookable;
      // And even if a course is scheduled at 5pm it may take a little more time (like 1,2 mins) until the book button appears
      this.durationToWaitAdditionalUntilACourseBecomesBookable = Duration.ofMillis(20000);
      this.courseBookerHelper = courseBookerHelper;
      this.waitForCourseTableToAppear = waitForCourseTableToAppear;
      this.waitForBookDialogToAppear = waitForBookDialogToAppear;
      this.waitForCourseContentToAppear = Duration.ofMillis(20000);
      missingBookingAndCloseButtonCallbackHandler = (courseName, errorHandler, courseDetails) -> {
         if (dryRun) {
            return CourseClickedResult.COURSE_BOOKING_ABORTED;
         }
         return this.handleBookingAndCloseButtonMissing(courseName, errorHandler, courseDetails);
      };
   }

   ////////////////////////// Select A Curse //////////////////////////////////

   /**
    * Tries to retrieve the {@link WebElement}-buttons for the filtered courses and select the course for the given name.
    * If there is only such button, this button is clicked. and the course booked if possible.
    * For a dry-run, {@link CourseClickedResult#COURSE_BOOKING_ABORTED} is then returned.
    * For a real booking the course is booked and {@link CourseClickedResult#COURSE_BOOKED} is returned if there is a booking button.
    * If there is a logging-button, the booking is done after the logging and {@link CourseClickedResult#COURSE_BOOKED} is returned as well.
    * This method returns {@link CourseClickedResult#COURSE_NOT_BOOKABLE} if there is neither of the two buttons.
    *
    * @param courseBookDetails {@link CourseBookDetails} with details about the course to book
    * @param errorHandler      the {@link ErrorHandler} to handle missing {@link WebElement}
    * @return a {@link CourseClickedResult} describing the outcome of the selecting procedure
    */
   public CourseClickedResult selectCourseAndBook(CourseBookDetails courseBookDetails, ErrorHandler errorHandler) {
      LOG.info("Trying to select course [{}]", courseBookDetails.courseName());
      boolean courseSelected = selectCourse(courseBookDetails, errorHandler);
      return clickSelectedCourseLoginIfNecessaryAndBook(courseSelected, courseBookDetails.courseName(), errorHandler);
   }

   private boolean selectCourse(CourseBookDetails courseBookDetails, ErrorHandler errorHandler) {
      List<WebElement> courseButtons = getFilteredCourseButtons();
      if (courseButtons.size() == 1) {
         LOG.info("Found one result, going to click the button");
         this.aquabasileaNavigatorHelper.clickButton(courseButtons.get(0), errorHandler);
         return true;
      } else {
         LOG.warn("No unique filter-results, found {} courses!", courseButtons.size());
         if (doBruteForceCourseSelecting(courseBookDetails, errorHandler)) {
            return true;
         }
         handleCourseNotFound(errorHandler, courseButtons, courseBookDetails.courseName());
         return false;
      }
   }

   private List<WebElement> getFilteredCourseButtons() {
      Optional<WebElement> courseTableOpt = getCourseTableWebElement();
      if (courseTableOpt.isPresent()) {
         return this.aquabasileaNavigatorHelper.findAllWebElementsByPredicateAndBy(courseTableOpt.get(), By.tagName(HTML_BUTTON_TYPE), webElement -> true);
      }
      LOG.error("course-area not visible, no selected courses available!");
      this.aquabasileaNavigatorHelper.takeScreenshot("no-course-table");
      return List.of();
   }

   private boolean doBruteForceCourseSelecting(CourseBookDetails courseBookDetails, ErrorHandler errorHandler) {
      LOG.warn("Try to find the right course by doing a brute-force search of all found course-buttons..");
      WebElement aquabasileaCourseButton = findAquabasileaCourseButton(courseBookDetails, errorHandler);
      if (nonNull(aquabasileaCourseButton)) {
         LOG.info("Course {} found within the filtered courses!", courseBookDetails.courseName());
         this.aquabasileaNavigatorHelper.clickButton(aquabasileaCourseButton, errorHandler);
      }
      return nonNull(aquabasileaCourseButton);
   }

   /**
    * If there are more than one filter-results, we assume that one or more filters couldn't be applied
    * Depending on which filters where applied (e.g. course location) the right course might be within all results.
    * So lets check them all in a brut-force-manner for a course with the right course name
    */
   private WebElement findAquabasileaCourseButton(CourseBookDetails courseBookDetails, ErrorHandler errorHandler) {
      this.aquabasileaNavigatorHelper.waitForVisibilityOfElement(WebNavigateUtil.createXPathBy(HTML_DIV_TYPE, WEB_ELEMENT_COURSE_RESULTS_CONTENT_ATTR_NAME, WEB_ELEMENT_COURSE_RESULTS_CONTENT_ATTR_VALUE), waitForCourseContentToAppear);
      AquabasileaCourseExtractorHelper aquabasileaCourseExtractorHelper = new AquabasileaCourseExtractorHelper(aquabasileaNavigatorHelper, errorHandler, waitForBookDialogToAppear);
      List<WebElement> allAquabasileaCourseWebElements = aquabasileaCourseExtractorHelper.findAllAquabasileaCourseButtons();
      LOG.info("Try to find matching course for booking details {}", courseBookDetails);
      for (WebElement aquabasileaCourseButton : allAquabasileaCourseWebElements) {
         AquabasileaCourse aquabasileaCourse = aquabasileaCourseExtractorHelper.evalCourseDetailsAndCreateAquabasileaCourse(aquabasileaCourseButton);
         if (isEquals(courseBookDetails, aquabasileaCourse)) {
            return aquabasileaCourseButton;
         } else {
            logWarningCourseNotMatching(courseBookDetails, aquabasileaCourse);
         }
      }
      return null;
   }

   private static boolean isEquals(CourseBookDetails courseBookDetails, AquabasileaCourse aquabasileaCourse) {
      return courseBookDetails.courseName().equals(aquabasileaCourse.courseName())
              && courseBookDetails.courseLocation().equals(aquabasileaCourse.courseLocation())
              && courseBookDetails.courseDate().equals(aquabasileaCourse.courseDate())
              && courseBookDetails.courseInstructor().equals(aquabasileaCourse.courseInstructor());
   }

   private Optional<WebElement> getCourseTableWebElement() {
      LOG.info("Waiting for the course-table to appear (max. {}s)", waitForCourseTableToAppear.toSeconds());
      By courseTableBy = WebNavigateUtil.createXPathBy(HTML_DIV_TYPE, WEB_ELEMENT_COURSE_RESULTS_CONTENT_ATTR_NAME, WEB_ELEMENT_COURSE_RESULTS_CONTENT_ATTR_VALUE);
      this.aquabasileaNavigatorHelper.waitForVisibilityOfElement(courseTableBy, waitForCourseTableToAppear);
      return this.aquabasileaNavigatorHelper.findWebElementBy(null, courseTableBy);
   }

   private CourseClickedResult clickSelectedCourseLoginIfNecessaryAndBook(boolean courseSelected, String courseName, ErrorHandler errorHandler) {
      if (courseSelected) {
         this.aquabasileaNavigatorHelper.waitForVisibilityOfElement(WebNavigateUtil.createXPathBy(HTML_DIV_TYPE, WEB_ELEMENT_BOOK_DIALOG_ATTR_NAME, WEB_ELEMENT_BOOK_DIALOG_ATTR_VALUE), waitForBookDialogToAppear);
         LOG.info("Course {} selected. Now either do booking or cancel", courseName);
         WebElement courseDetails = this.aquabasileaNavigatorHelper.getWebElementByNameTagNameAndValue(null, HTML_DIV_TYPE, WEB_ELEMENT_BOOK_DIALOG_ATTR_NAME, WEB_ELEMENT_BOOK_DIALOG_ATTR_VALUE);
         Optional<WebElement> bookCourseButtonOpt = this.aquabasileaNavigatorHelper.findWebElementByTageNameAndInnerHtmlValue(courseDetails, HTML_BUTTON_TYPE, WEB_ELEMENT_BOOK_SPOT_BUTTON_TEXT);
         if (bookCourseButtonOpt.isPresent()) {
            return courseBookerHelper.cancelOrBookCourse(bookCourseButtonOpt.get(), errorHandler);
         } else {
            Optional<WebElement> courseAlreadyBookedHint = this.aquabasileaNavigatorHelper.findWebElementByTageNameAndInnerHtmlValue(courseDetails, HTML_DIV_TYPE, WEB_ELEMENT_COURSE_ALREADY_BOOKED_VALUE);
            if (courseAlreadyBookedHint.isPresent()) {
               LOG.warn("Course is fully booked..");
               handleBookButtonNotAvailable(courseName, errorHandler, courseDetails);
               return CourseClickedResult.COURSE_NOT_BOOKABLE;
            }
            LOG.warn("Neither booking nor login button present..");
            return missingBookingAndCloseButtonCallbackHandler.handleBookingAndCloseButtonMissing(courseName, errorHandler, courseDetails);
         }
      }
      return CourseClickedResult.COURSE_NOT_SELECTED_NO_SINGLE_RESULT;
   }

   private CourseClickedResult handleBookingAndCloseButtonMissing(String courseName, ErrorHandler errorHandler, WebElement courseDetails) {
      // Booking & Login-button is missing..
      long millis2WaitUntilCourseBecomesBookable = this.duration2WaitUntilCourseBecomesBookable.get().toMillis();
      long millis2Wait = millis2WaitUntilCourseBecomesBookable + durationToWaitAdditionalUntilACourseBecomesBookable.toMillis();
      if (millis2Wait < 0) {
         LOG.info("Time is up, mission abort! time2WaitUntilCourseBecomesBookable={}", millis2WaitUntilCourseBecomesBookable);
         // and we have to assume, that the course is already bookable. Meaning: The course is already full (or we are not logged in)
         handleBookButtonNotAvailable(courseName, errorHandler, courseDetails);
         return CourseClickedResult.COURSE_NOT_BOOKABLE;
      } else {
         LOG.info("Time is not yet up. Retry again in {}ms", millis2WaitUntilCourseBecomesBookable);
         // but we also have to wait, until the course is bookable (24h before the course takes place)
         getCloseBookingDialogButtonAndClick(courseDetails);
         return CourseClickedResult.COURSE_NOT_BOOKED_RETRY;
      }
   }

   private void handleBookButtonNotAvailable(String courseName, ErrorHandler errorHandler, WebElement courseDetails) {
      this.aquabasileaNavigatorHelper.takeScreenshot("no-booking-button");
      getCloseBookingDialogButtonAndClick(courseDetails);
      String errorMsg = String.format("Booking Button not found! The course '%s' is not bookable", courseName);
      errorHandler.handleError(errorMsg);
   }

   private void getCloseBookingDialogButtonAndClick(WebElement courseDetails) {
      WebElement closeBookingDialogButton = this.aquabasileaNavigatorHelper.getWebElementByTageNameAndInnerHtmlValue(courseDetails, HTML_BUTTON_TYPE, WEB_ELEMENT_CLOSE_BOOK_COURSE_BUTTON_TEXT);
      closeBookingDialogButton.click();
   }

   private void handleCourseNotFound(ErrorHandler errorHandler, List<WebElement> courseButtons, String courseName) {
      String errorMsg;
      if (courseButtons.isEmpty()) {
         errorMsg = String.format("No courses found for course name '%s'", courseName);
      } else {
         errorMsg = String.format("Course '%s' could not be found! Filter result contains %s results!", courseName, courseButtons.size());
      }
      this.aquabasileaNavigatorHelper.takeScreenshot(String.format("course '%s' selection", courseName));
      errorHandler.handleError(errorMsg);
   }

   private static void logWarningCourseNotMatching(CourseBookDetails courseBookDetails, AquabasileaCourse aquabasileaCourse) {
      LOG.warn("No match for course {}!", aquabasileaCourse);
      LOG.debug("Course-name is equal: {}", courseBookDetails.courseName().equals(aquabasileaCourse.courseName()));
      LOG.debug("Course-location is equal: {}", courseBookDetails.courseLocation().equals(aquabasileaCourse.courseLocation()));
      LOG.debug("Course-date is equal: {}", courseBookDetails.courseDate().equals(aquabasileaCourse.courseDate()));
      LOG.debug("Course-instructor is equal: {}", courseBookDetails.courseInstructor().equals(aquabasileaCourse.courseInstructor()));
   }

   private interface BookingAndCloseButtonMissingCallbackHandler {
      CourseClickedResult handleBookingAndCloseButtonMissing(String courseName, ErrorHandler errorHandler, WebElement courseDetails);
   }
}
