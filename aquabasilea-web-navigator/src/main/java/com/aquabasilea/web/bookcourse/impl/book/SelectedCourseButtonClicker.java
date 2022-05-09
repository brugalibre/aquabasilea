package com.aquabasilea.web.bookcourse.impl.book;

import com.aquabasilea.web.error.ErrorHandler;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseClickedResult;
import org.openqa.selenium.WebElement;

import java.util.Optional;

public interface SelectedCourseButtonClicker {

   /**
    * Closes the booking dialog or books the selected course
    *
    * @param closeBookingDialogButton the button to close the booking dialog
    * @param bookCourseButtonOpt      the optional button to book the selected course
    * @param errorHandler             the {@link ErrorHandler} to handle any missing button
    * @return a {@link CourseClickedResult} which defines either the course was booked or the dialog closed
    */
   CourseClickedResult cancelOrBookCourse(WebElement closeBookingDialogButton, Optional<WebElement> bookCourseButtonOpt, ErrorHandler errorHandler);
}
