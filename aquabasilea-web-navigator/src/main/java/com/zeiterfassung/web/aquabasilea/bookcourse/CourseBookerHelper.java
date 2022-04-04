package com.zeiterfassung.web.aquabasilea.bookcourse;

import com.zeiterfassung.web.aquabasilea.error.ErrorHandler;
import com.zeiterfassung.web.aquabasilea.navigate.AquabasileaNavigatorHelper;
import com.zeiterfassung.web.aquabasilea.selectcourse.result.CourseClickedResult;
import com.zeiterfassung.web.common.navigate.util.WebNavigateUtil;
import org.openqa.selenium.WebElement;

import java.util.Optional;

import static com.zeiterfassung.web.aquabasilea.constant.AquabasileaWebConst.*;
import static com.zeiterfassung.web.common.constant.BaseWebConst.HTML_BUTTON_TYPE;

public class CourseBookerHelper {

   public static final int RETRIES = 2;
   private final SelectedCourseButtonClicker selectedCourseButtonClicker;
   private AquabasileaNavigatorHelper aquabasileaNavigatorHelper;

   public CourseBookerHelper(AquabasileaNavigatorHelper aquabasileaNavigatorHelper, boolean dryRun) {
      this.aquabasileaNavigatorHelper = aquabasileaNavigatorHelper;
      this.selectedCourseButtonClicker = getSelectedCourseButtonClicker(dryRun);
   }

   /**
    * Finally cancel the booking or book the selected course
    *
    * @param bookCourseButtonOpt the optional button to book the selected course
    * @param errorHandler the {@link ErrorHandler}
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
            this.aquabasileaNavigatorHelper.clickButtonOrHandleError(bookButton, () -> bookButton, errorHandler, WEB_ELEMENT_BOOK_SPOT_BUTTON_TEXT);
            bookButton.ifPresent(webElement ->  waitForCancelButtonToAppear(RETRIES));
            abortButton.click();
            return CourseClickedResult.COURSE_BOOKED;
         };
      }
   }

   private void waitForCancelButtonToAppear(int amountOfRetries) {
      Optional<WebElement> cancelCourseButtonOpt = this.aquabasileaNavigatorHelper.findWebElementByTageNameAndInnerHtmlValue(null, HTML_BUTTON_TYPE, WEB_ELEMENT_CANCEL_SPOT_BUTTON_TEXT);

      if (cancelCourseButtonOpt.isEmpty() && amountOfRetries >= RETRIES) {
         WebNavigateUtil.waitForMilliseconds(500);
         waitForCancelButtonToAppear(amountOfRetries - 1);
      }
   }
}
