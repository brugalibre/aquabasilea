package com.aquabasilea.web.bookcourse.impl.book;

import com.aquabasilea.web.bookcourse.impl.select.result.CourseClickedResult;
import com.aquabasilea.web.error.ErrorHandler;
import org.openqa.selenium.WebElement;

import java.util.Optional;

/**
 * The {@link SelectedCourseHandler} does the actual action, after a certain course is selected and clicked
 * Depending on if it's a dry run or an actual booking, the booking dialog is either closed or the course booked
 */
public interface SelectedCourseHandler {

   /**
    * Closes the booking dialog or books the selected course
    *
    * @param bookCourseButtonOpt the optional button to book the selected course
    * @param errorHandler        the {@link ErrorHandler} to handle any missing button
    * @return a {@link CourseClickedResult} which defines either the course was booked or the dialog closed
    */
   CourseClickedResult cancelOrBookCourse(Optional<WebElement> bookCourseButtonOpt, ErrorHandler errorHandler);
}
