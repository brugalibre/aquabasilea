package com.aquabasilea.web.bookcourse.impl.select;

import com.aquabasilea.web.bookcourse.impl.book.CourseBookerHelper;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseClickedResult;
import com.aquabasilea.web.constant.AquabasileaWebConst;
import com.aquabasilea.web.error.ErrorHandler;
import com.aquabasilea.web.login.AquabasileaLoginHelper;
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
   private static final Duration DURATION_TO_WAIT_ADDITIONAL_UNTIL_A_COURSE_BECAME_BOOKABLE = Duration.ofSeconds(60);
   private final Supplier<Duration> duration2WaitUntilCourseBecomesBookable;
   private final CourseBookerHelper courseBookerHelper;
   private final AquabasileaLoginHelper aquabasileaLoginHelper;
   private final AquabasileaNavigatorHelper aquabasileaNavigatorHelper;
   private final BookingAndCloseButtonMissingCallbackHandler missingBookingAndCloseButtonCallbackHandler;

   public CourseSelectHelper(CourseBookerHelper courseBookerHelper, AquabasileaLoginHelper aquabasileaLoginHelper,
                             AquabasileaNavigatorHelper aquabasileaNavigatorHelper, Supplier<Duration> duration2WaitUntilCourseBecomesBookable,
                             boolean dryRun) {
      this.aquabasileaNavigatorHelper = aquabasileaNavigatorHelper;
      this.aquabasileaLoginHelper = aquabasileaLoginHelper;
      // It may happen, that the AquabasileaWebNavigator was started slightly to early and has to wait, until the booking button becomes available
      this.duration2WaitUntilCourseBecomesBookable = duration2WaitUntilCourseBecomesBookable;
      this.courseBookerHelper = courseBookerHelper;
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
    * @param courseName   the name of the course to book
    * @param errorHandler the {@link ErrorHandler} to handle missing {@link WebElement}
    * @return a {@link CourseClickedResult} describing the outcome of the selecting procedure
    */
   public CourseClickedResult selectCourseAndBook(String courseName, ErrorHandler errorHandler) {
      LOG.info("Trying to select course '{}'", courseName);
      boolean courseSelected = selectCourse(courseName, errorHandler);
      return clickSelectedCourseLoginIfNecessaryAndBook(courseSelected, courseName, errorHandler);
   }

   private boolean selectCourse(String courseName, ErrorHandler errorHandler) {
      List<WebElement> courseButtons = getCourseButtons();
      LOG.info("Course '{}' selected, found {} results", courseName, courseButtons.size());
      if (courseButtons.size() == 1) {
         this.aquabasileaNavigatorHelper.clickButton(courseButtons.get(0), errorHandler);
         return true;
      } else {
         handleCourseNotFound(errorHandler, courseButtons, courseName);
         return false;
      }
   }

   private List<WebElement> getCourseButtons() {
      Optional<WebElement> courseTableOpt = getCourseTableWebElement();
      if (courseTableOpt.isPresent()) {
         return this.aquabasileaNavigatorHelper.findAllWebElementsByPredicateAndBy(courseTableOpt.get(), By.tagName(HTML_BUTTON_TYPE), webElement -> true);
      }
      LOG.error("course-area not visible, no selected courses available!");
      this.aquabasileaNavigatorHelper.takeScreenshot("no-course-table");
      return List.of();
   }

   private Optional<WebElement> getCourseTableWebElement() {
      LOG.info("Waiting {}s for the course-table to appear..", WAIT_FOR_COURSE_TABLE_TO_APPEAR.toSeconds());
      By courseTableBy = WebNavigateUtil.createXPathBy(HTML_DIV_TYPE, WEB_ELEMENT_COURSE_RESULTS_CONTENT_ATTR_NAME, WEB_ELEMENT_COURSE_RESULTS_CONTENT_ATTR_VALUE);
      this.aquabasileaNavigatorHelper.waitForVisibilityOfElement(courseTableBy, WAIT_FOR_COURSE_TABLE_TO_APPEAR.toMillis());
      return this.aquabasileaNavigatorHelper.findWebElementBy(null, courseTableBy);
   }

   private CourseClickedResult clickSelectedCourseLoginIfNecessaryAndBook(boolean courseSelected, String courseName, ErrorHandler errorHandler) {
      if (courseSelected) {
         this.aquabasileaNavigatorHelper.waitForVisibilityOfElement(WebNavigateUtil.createXPathBy(HTML_DIV_TYPE, WEB_ELEMENT_BOOK_DIALOG_ATTR_NAME, WEB_ELEMENT_BOOK_DIALOG_ATTR_VALUE), 10000);
         LOG.info("Course selected..");
         WebElement courseDetails = this.aquabasileaNavigatorHelper.getWebElementByNameTagNameAndValue(null, HTML_DIV_TYPE, WEB_ELEMENT_BOOK_DIALOG_ATTR_NAME, WEB_ELEMENT_BOOK_DIALOG_ATTR_VALUE);
         Optional<WebElement> loginButton = this.aquabasileaNavigatorHelper.findWebElementByNameTagNameAndValue(courseDetails, HTML_BUTTON_TYPE, WEB_ELEMENT_LOGIN_SELECT_COURSE_ANMELDE_BUTTON_ATTR_ID, WEB_ELEMENT_LOGIN_SELECT_COURSE_ANMELDE_BUTTON_ATTR_ID_TEXT);
         Optional<WebElement> bookCourseButtonOpt = this.aquabasileaNavigatorHelper.findWebElementByTageNameAndInnerHtmlValue(courseDetails, HTML_BUTTON_TYPE, WEB_ELEMENT_BOOK_SPOT_BUTTON_TEXT);
         if (bookCourseButtonOpt.isPresent()) {
            return courseBookerHelper.cancelOrBookCourse(bookCourseButtonOpt, errorHandler);
         } else if (loginButton.isPresent()) {
            aquabasileaLoginHelper.login(loginButton.get());
            return courseBookerHelper.cancelOrBookCourse(bookCourseButtonOpt, errorHandler);
         } else {
            LOG.warn("Neither booking nor login button present..");
            return missingBookingAndCloseButtonCallbackHandler.handleBookingAndCloseButtonMissing(courseName, errorHandler, courseDetails);
         }
      }
      return CourseClickedResult.COURSE_NOT_SELECTED_NO_SINGLE_RESULT;
   }

   private CourseClickedResult handleBookingAndCloseButtonMissing(String courseName, ErrorHandler errorHandler, WebElement courseDetails) {
      // Booking & Login-button is missing..
      long millis2WaitUntilCourseBecomesBookable = this.duration2WaitUntilCourseBecomesBookable.get().toMillis();
      long millis2Wait = millis2WaitUntilCourseBecomesBookable + DURATION_TO_WAIT_ADDITIONAL_UNTIL_A_COURSE_BECAME_BOOKABLE.toMillis();
      if (millis2Wait < 0) {
         LOG.info("Time is up, course seems to be fully booked. time2WaitUntilCourseBecomesBookable={}", millis2WaitUntilCourseBecomesBookable);
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
      getCloseBookingDialogButtonAndClick(courseDetails);
      this.aquabasileaNavigatorHelper.takeScreenshot("no-booking-button");
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

   private interface BookingAndCloseButtonMissingCallbackHandler {
      CourseClickedResult handleBookingAndCloseButtonMissing(String courseName, ErrorHandler errorHandler, WebElement courseDetails);
   }
}
