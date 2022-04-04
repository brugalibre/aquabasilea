package com.zeiterfassung.web.aquabasilea.selectcourse;

import com.zeiterfassung.web.aquabasilea.bookcourse.CourseBookerHelper;
import com.zeiterfassung.web.aquabasilea.error.ErrorHandler;
import com.zeiterfassung.web.aquabasilea.login.AquabasileaLoginHelper;
import com.zeiterfassung.web.aquabasilea.navigate.AquabasileaNavigatorHelper;
import com.zeiterfassung.web.aquabasilea.navigate.AquabasileaWebNavigatorImpl;
import com.zeiterfassung.web.aquabasilea.selectcourse.result.CourseClickedResult;
import com.zeiterfassung.web.common.navigate.util.WebNavigateUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.LongSupplier;

import static com.zeiterfassung.web.aquabasilea.constant.AquabasileaWebConst.*;
import static com.zeiterfassung.web.common.constant.BaseWebConst.HTML_BUTTON_TYPE;
import static com.zeiterfassung.web.common.constant.BaseWebConst.HTML_DIV_TYPE;

/**
 * The {@link CourseSelectHelper} does the actual selecting of the course. Acourse becomes first bookable 24h before.
 * The {@link AquabasileaWebNavigatorImpl} starts a little earlier when
 * doing the final booking
 *
 * First we try to select and book a course, after we entered the filter criteria and applied the filter.
 * If it is not yet bookable, but we are too early anyway, we sleep until the course is bookable (
 * minus the time required to reload the entire page).
 *
 * After that, if the course is still not yet bookable
 * we try again and again, after a certain timeout. After this time, we can safely assume, that the course we want
 * to book is already booked
 */
public class CourseSelectHelper {

   private static final Logger LOG = LoggerFactory.getLogger(CourseSelectHelper.class);

   /**
    * This is the additional time we wait after the course became bookable
    */
   private static final int SECONDS_TO_WAIT_ADDITIONAL_UNTIL_A_COURSE_BECAME_BOOKABLE = 60;
   private final LongSupplier time2WaitUntilCourseBecomesBookable;
   private final CourseBookerHelper courseBookerHelper;
   private final AquabasileaLoginHelper aquabasileaLoginHelper;
   private final AquabasileaNavigatorHelper aquabasileaNavigatorHelper;
   private final Runnable pageRefresher;
   private BookingAndCloseButtonMissingCallbackHandler missingBookingAndCloseButtonCallbackHandler;

   public CourseSelectHelper(CourseBookerHelper courseBookerHelper, AquabasileaLoginHelper aquabasileaLoginHelper,
                             AquabasileaNavigatorHelper aquabasileaNavigatorHelper, LongSupplier time2WaitUntilCourseBecomesBookable,
                             boolean dryRun, Runnable pageRefresher) {
      this.aquabasileaNavigatorHelper = aquabasileaNavigatorHelper;
      this.aquabasileaLoginHelper = aquabasileaLoginHelper;
      // It may happen, that the AquabasileaWebNavigator was started slightly to early and has to wait, until the booking button becomes available
      this.time2WaitUntilCourseBecomesBookable = time2WaitUntilCourseBecomesBookable;
      this.courseBookerHelper = courseBookerHelper;
      this.pageRefresher = pageRefresher;
      if (dryRun) {
         missingBookingAndCloseButtonCallbackHandler = (courseName, errorHandler, courseDetails) -> CourseClickedResult.COURSE_BOOKING_ABORTED;
      } else {
         missingBookingAndCloseButtonCallbackHandler = this::handleBookingAndCloseButtonMissing;
      }
   }

   ////////////////////////// Select A Curse //////////////////////////////////

   public CourseClickedResult selectCourseAndBook(String courseName, ErrorHandler errorHandler) {
      boolean courseSelected = selectCourse(courseName, errorHandler);
      CourseClickedResult courseClickedResult = clickSelectedCourseLoginIfNecessaryAndBook(courseSelected, courseName, errorHandler);
      if (courseClickedResult == CourseClickedResult.COURSE_NOT_BOOKED_RETRY) {
         String errorMsg = String.format("Course '%s' not yet available, %sms left, do retry", courseName, time2WaitUntilCourseBecomesBookable.getAsLong());
         errorHandler.handleError(errorMsg);
         WebNavigateUtil.waitForMilliseconds((int) (time2WaitUntilCourseBecomesBookable.getAsLong() - PAGE_REFRESH_DURATION.toMillis()));
         this.pageRefresher.run();
         LOG.info("Page refreshed, try to select course again");
         courseClickedResult = selectCourseAndBook(courseName, errorHandler);
      }
      return courseClickedResult;
   }

   private boolean selectCourse(String courseName, ErrorHandler errorHandler) {
      this.aquabasileaNavigatorHelper.waitForVisibilityOfElement(WebNavigateUtil.createXPathBy(HTML_DIV_TYPE, WEB_ELEMENT_COURSE_RESULTS_CONTENT_ATTR_NAME, WEB_ELEMENT_COURSE_RESULTS_CONTENT_ATTR_VALUE), 20000);
      WebElement courseArea = this.aquabasileaNavigatorHelper.findWebElementBy(null, WebNavigateUtil.createXPathBy(HTML_DIV_TYPE, WEB_ELEMENT_COURSE_RESULTS_CONTENT_ATTR_NAME, WEB_ELEMENT_COURSE_RESULTS_CONTENT_ATTR_VALUE)).get();
      List<WebElement> courseButtons = this.aquabasileaNavigatorHelper.findAllWebElementsByPredicateAndBy(courseArea, By.tagName(HTML_BUTTON_TYPE), webElement -> true);
      if (courseButtons.size() == 1) {
         courseButtons.get(0).click();
         return true;
      } else {
         handleCourseNotFound(errorHandler, courseButtons, courseName);
         return false;
      }
   }

   private CourseClickedResult clickSelectedCourseLoginIfNecessaryAndBook(boolean courseSelected, String courseName, ErrorHandler errorHandler) {
      if (courseSelected) {
         this.aquabasileaNavigatorHelper.waitForVisibilityOfElement(WebNavigateUtil.createXPathBy(HTML_DIV_TYPE, WEB_ELEMENT_BOOK_DIALOG_ATTR_NAME, WEB_ELEMENT_BOOK_DIALOG_ATTR_VALUE), 10000);
         LOG.info("Course selected..");
         WebElement courseDetails = this.aquabasileaNavigatorHelper.getWebElementByNameTagNameAndValue(null, HTML_DIV_TYPE, WEB_ELEMENT_BOOK_DIALOG_ATTR_NAME, WEB_ELEMENT_BOOK_DIALOG_ATTR_VALUE);
         Optional<WebElement> loginButton = this.aquabasileaNavigatorHelper.findWebElementByNameTagNameAndValue(courseDetails, HTML_BUTTON_TYPE, WEB_ELEMENT_LOGIN_SELECT_COURSE_ANMELDE_BUTTON_ATTR_ID, WEB_ELEMENT_LOGIN_SELECT_COURSE_ANMELDE_BUTTON_ATTR_ID_TEXT);
         Optional<WebElement> bookCourseButtonOpt = this.aquabasileaNavigatorHelper.findWebElementByTageNameAndInnerHtmlValue(null, HTML_BUTTON_TYPE, WEB_ELEMENT_BOOK_SPOT_BUTTON_TEXT);
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
      if (this.time2WaitUntilCourseBecomesBookable.getAsLong() + SECONDS_TO_WAIT_ADDITIONAL_UNTIL_A_COURSE_BECAME_BOOKABLE < 0) {
         // and we have to assume, that the course is already bookable. Meaning: The course is already full (or we are not logged in)
         handleBookButtonNotAvailable(courseName, errorHandler, courseDetails);
         return CourseClickedResult.COURSE_NOT_BOOKABLE;
      } else {
         // but we also have to wait, until the course is bookable (24h before the course takes place)
         WebElement cancelBookingButton = this.aquabasileaNavigatorHelper.getWebElementByTageNameAndInnerHtmlValue(null, HTML_BUTTON_TYPE, WEB_ELEMENT_CLOSE_BOOK_COURSE_BUTTON_TEXT);
         cancelBookingButton.click();
         return CourseClickedResult.COURSE_NOT_BOOKED_RETRY;
      }
   }

   private void handleBookButtonNotAvailable(String courseName, ErrorHandler errorHandler, WebElement courseDetails) {
      WebElement closeBookingDialogButton = this.aquabasileaNavigatorHelper.getWebElementByTageNameAndInnerHtmlValue(courseDetails, HTML_BUTTON_TYPE, WEB_ELEMENT_CLOSE_BOOK_COURSE_BUTTON_TEXT);
      closeBookingDialogButton.click();
      String errorMsg = String.format("Booking Button not found! The course '%s' is not bookable", courseName);
      errorHandler.handleError(errorMsg);
   }

   private static void handleCourseNotFound(ErrorHandler errorHandler, List<WebElement> courseButtons, String courseName) {
      String errorMsg;
      if (courseButtons.isEmpty()) {
         errorMsg = String.format("No courses found for course name '%s'", courseName);
      } else {
         errorMsg = String.format("Course '%s' could not be found! Filter result contains %s results!", courseName, courseButtons.size());
      }
      errorHandler.handleError(errorMsg);
   }

   private interface BookingAndCloseButtonMissingCallbackHandler {
      CourseClickedResult handleBookingAndCloseButtonMissing(String courseName, ErrorHandler errorHandler, WebElement courseDetails);
   }
}
