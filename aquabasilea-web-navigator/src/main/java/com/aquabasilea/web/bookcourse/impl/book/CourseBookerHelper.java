package com.aquabasilea.web.bookcourse.impl.book;

import com.aquabasilea.web.bookcourse.impl.select.result.CourseClickedResult;
import com.aquabasilea.web.error.ErrorHandler;
import com.aquabasilea.web.navigate.AquabasileaNavigatorHelper;
import com.zeiterfassung.web.common.navigate.util.WebNavigateUtil;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static com.aquabasilea.web.constant.AquabasileaWebConst.*;
import static com.zeiterfassung.web.common.constant.BaseWebConst.HTML_BUTTON_TYPE;
import static com.zeiterfassung.web.common.constant.BaseWebConst.HTML_DIV_TYPE;

public class CourseBookerHelper {

   private static final Logger LOG = LoggerFactory.getLogger(CourseBookerHelper.class);
   private static final int RETRIES = 2;
   private final SelectedCourseButtonClicker selectedCourseButtonClicker;
   private final AquabasileaNavigatorHelper aquabasileaNavigatorHelper;

   public CourseBookerHelper(AquabasileaNavigatorHelper aquabasileaNavigatorHelper, boolean dryRun) {
      this.aquabasileaNavigatorHelper = aquabasileaNavigatorHelper;
      this.selectedCourseButtonClicker = getSelectedCourseButtonClicker(dryRun);
   }

   /**
    * Finally cancel the booking or book the selected course
    *
    * @param bookCourseButtonOpt the optional button to book the selected course
    * @param errorHandler        the {@link ErrorHandler}
    * @return a {@link CourseClickedResult} depending on weather we canceled or booked
    */
   public CourseClickedResult cancelOrBookCourse(Optional<WebElement> bookCourseButtonOpt, ErrorHandler errorHandler) {
      WebElement cancelBookingButton = aquabasileaNavigatorHelper.getWebElementByTageNameAndInnerHtmlValue(null, HTML_BUTTON_TYPE, WEB_ELEMENT_CLOSE_BOOK_COURSE_BUTTON_TEXT);
      return selectedCourseButtonClicker.cancelOrBookCourse(cancelBookingButton, bookCourseButtonOpt, errorHandler);
   }

   private SelectedCourseButtonClicker getSelectedCourseButtonClicker(boolean dryRun) {
      if (dryRun) {
         return (abortButton, bookButton, errorHandler) -> {
            abortButton.click();
            return CourseClickedResult.COURSE_BOOKING_ABORTED;
         };
      } else {
         return (abortButton, bookButton, errorHandler) -> {
            this.aquabasileaNavigatorHelper.clickButtonOrHandleError(() -> bookButton, errorHandler, WEB_ELEMENT_BOOK_SPOT_BUTTON_TEXT);
            bookButton.ifPresent(webElement -> waitForCancelButtonToAppear(RETRIES));
            abortButton.click();
            return CourseClickedResult.COURSE_BOOKED;
         };
      }
   }

   private void waitForCancelButtonToAppear(int amountOfRetries) {
      WebElement courseDetails = this.aquabasileaNavigatorHelper.getWebElementByNameTagNameAndValue(null, HTML_DIV_TYPE, WEB_ELEMENT_BOOK_DIALOG_ATTR_NAME, WEB_ELEMENT_BOOK_DIALOG_ATTR_VALUE);
      Optional<WebElement> cancelCourseButtonOpt = this.aquabasileaNavigatorHelper.findWebElementByTageNameAndInnerHtmlValue(courseDetails, HTML_BUTTON_TYPE, WEB_ELEMENT_CANCEL_SPOT_BUTTON_TEXT);
      if (cancelCourseButtonOpt.isEmpty() && amountOfRetries > 0) {
         LOG.info("Cancel button not available, retry. Retries left {}", amountOfRetries - 1);
         WebNavigateUtil.waitForMilliseconds(500);
         waitForCancelButtonToAppear(amountOfRetries - 1);
      } else if (cancelCourseButtonOpt.isPresent()) {
         LOG.info("Cancel button available. Close dialog, we are done here");
      } else {
         LOG.info("Cancel button not available, no retries left");
      }
   }
}
