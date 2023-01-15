package com.aquabasilea.web.bookcourse.impl.book;

import com.aquabasilea.web.bookcourse.impl.select.result.CourseClickedResult;
import com.aquabasilea.web.error.ErrorHandler;
import com.aquabasilea.web.navigate.AquabasileaNavigatorHelper;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static com.aquabasilea.web.constant.AquabasileaWebConst.*;
import static com.zeiterfassung.web.common.constant.BaseWebConst.HTML_BUTTON_TYPE;
import static com.zeiterfassung.web.common.constant.BaseWebConst.HTML_DIV_TYPE;

public class CourseBookerHelper {

   private static final Logger LOG = LoggerFactory.getLogger(CourseBookerHelper.class);
   private final SelectedCourseHandler selectedCourseHandler;
   private final AquabasileaNavigatorHelper aquabasileaNavigatorHelper;

   public CourseBookerHelper(AquabasileaNavigatorHelper aquabasileaNavigatorHelper, boolean dryRun) {
      this.aquabasileaNavigatorHelper = aquabasileaNavigatorHelper;
      this.selectedCourseHandler = getSelectedCourseHandler(dryRun);
   }

   /**
    * Finally cancel the booking or book the selected course
    *
    * @param bookCourseButton the button to book the selected course
    * @param errorHandler     the {@link ErrorHandler}
    * @return a {@link CourseClickedResult} depending on weather we canceled or booked
    */
   public CourseClickedResult cancelOrBookCourse(WebElement bookCourseButton, ErrorHandler errorHandler) {
      return selectedCourseHandler.cancelOrBookCourse(bookCourseButton, errorHandler);
   }

   private SelectedCourseHandler getSelectedCourseHandler(boolean dryRun) {
      if (dryRun) {
         return (bookButton, errorHandler) -> {
            closeCourseDialog(errorHandler);
            LOG.info("Dry run successfully!");
            return CourseClickedResult.COURSE_BOOKING_ABORTED;
         };
      } else {
         return (bookButton, errorHandler) -> {
            this.aquabasileaNavigatorHelper.clickButtonOrHandleError(() -> Optional.of(bookButton), errorHandler, WEB_ELEMENT_BOOK_SPOT_BUTTON_TEXT);
            closeCourseDialog(errorHandler);
            LOG.info("Course booking successfully!");
            return CourseClickedResult.COURSE_BOOKED;
         };
      }
   }

   /*
    * Gets the cancel-button, presses it and closes therefore the dialog. In case of a dry run, this is straight forward.
    * For a real booking, it's a bit more complicated:
    * After booking the course, the dialog may be refreshed, which can lead to a StaleElementException. But if this is not a dry run anyway,
    * the booking was actually successfully, and we could not care less.
    *
    * Throwing an Exception would lead in a wrong CourseClickedResult
    */
   private void closeCourseDialog(ErrorHandler errorHandler) {
      LOG.debug("Trying to close the dialog");
      WebElement cancelBookingButton = getOptionalCancelBooking();
      aquabasileaNavigatorHelper.clickButton(cancelBookingButton, errorHandler);
   }

   private WebElement getOptionalCancelBooking() {
      WebElement courseDetails = this.aquabasileaNavigatorHelper.getWebElementByNameTagNameAndValue(null, HTML_DIV_TYPE, WEB_ELEMENT_BOOK_DIALOG_ATTR_NAME, WEB_ELEMENT_BOOK_DIALOG_ATTR_VALUE);
      return aquabasileaNavigatorHelper.getWebElementByTageNameAndInnerHtmlValue(courseDetails, HTML_BUTTON_TYPE, WEB_ELEMENT_CLOSE_BOOK_COURSE_BUTTON_TEXT);
   }
}
