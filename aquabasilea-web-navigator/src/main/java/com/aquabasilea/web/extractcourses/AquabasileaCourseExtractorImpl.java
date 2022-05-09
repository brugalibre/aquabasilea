package com.aquabasilea.web.extractcourses;

import com.aquabasilea.web.bookcourse.impl.book.CourseBookerHelper;
import com.aquabasilea.web.error.ErrorHandler;
import com.aquabasilea.web.error.ErrorHandlerImpl;
import com.aquabasilea.web.filtercourse.CourseFilterHelper;
import com.aquabasilea.web.navigate.AbstractAquabasileaWebNavigator;
import com.aquabasilea.web.bookcourse.AquabasileaWebCourseBooker;
import com.aquabasilea.web.bookcourse.impl.select.CourseSelectHelper;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseBookingEndResult;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseBookingEndResult.CourseBookingEndResultBuilder;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseClickedResult;
import com.aquabasilea.web.util.ErrorUtil;
import com.zeiterfassung.web.common.navigate.util.WebNavigateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.Duration;
import java.util.function.Supplier;

import static com.aquabasilea.web.constant.AquabasileaWebConst.*;
import static com.aquabasilea.web.bookcourse.impl.select.result.CourseClickedResult.COURSE_NOT_SELECTED_EXCEPTION_OCCURRED;
import static com.zeiterfassung.web.common.constant.BaseWebConst.HTML_DIV_TYPE;

public class AquabasileaCourseExtractorImpl extends AbstractAquabasileaWebNavigator implements AquabasileaWebCourseBooker {

   private static final Logger LOG = LoggerFactory.getLogger(AquabasileaCourseExtractorImpl.class);
   private CourseSelectHelper courseSelectHelper;
   private CourseFilterHelper courseFilterHelper;

   public AquabasileaCourseExtractorImpl(String userName, String userPassword, String propertiesName) {
      super(userName, userPassword, propertiesName);
   }

   public static AquabasileaWebCourseBooker createAndInitAquabasileaWebNavigator(String userName, String userPassword, boolean dryRun, Supplier<Duration> duration2WaitUntilCourseBecomesBookable) {
      AquabasileaCourseExtractorImpl aquabasileaWebNavigator = new AquabasileaCourseExtractorImpl(userName, userPassword, AQUABASILEA_WEB_KURS_BUCHER_PROPERTIES);
      aquabasileaWebNavigator.initWebDriver();
      aquabasileaWebNavigator.init(dryRun, duration2WaitUntilCourseBecomesBookable);
      return aquabasileaWebNavigator;
   }

   @Override
   public void navigateToPageAndLogin() {
      this.webNavigatorHelper.navigateWithRetry(4, super::navigateToPageAndLogin, "navigateToPageAndLogin");
   }

   @Override
   public CourseBookingEndResult selectAndBookCourse(String courseName, DayOfWeek dayOfWeek) {
      ErrorHandler errorHandler = new ErrorHandlerImpl();
      try {
         return selectAndBookCourse(courseName, dayOfWeek, errorHandler);
      } catch (Exception e) {
         LOG.error("Error during course booking!", e);
         return handleExceptionAndBuildResult(courseName, errorHandler, e);
      }
   }

   private CourseBookingEndResult selectAndBookCourse(String courseName, DayOfWeek dayOfWeek, ErrorHandler errorHandler) {
      navigateToPageAndLogin();
      navigate2CoursePageWithRetry();
      courseFilterHelper.applyCriteriaFilter(courseName, dayOfWeek, errorHandler);
      CourseClickedResult courseClickedResult = courseSelectHelper.selectCourseAndBook(courseName, errorHandler);
      logout();
      return buildCourseBookingEndResult(courseName, errorHandler, null, courseClickedResult);
   }

   private void navigate2CoursePageWithRetry() {
      this.webNavigatorHelper.navigateWithRetry(4, () -> navigate2CoursePageInternal(true), "navigate2CoursePageInternal");
   }

   private void navigate2CoursePage() {
      navigate2CoursePageInternal(false);
   }

   private void navigate2CoursePageInternal(boolean clickLoginButton) {
      navigateToPage(coursePage);
      if (clickLoginButton) {
         aquabasileaLoginHelper.clickLoginButton();
      }
      // yes, this may take a veeeeeeeeeeeeery long time
      waitForVisibilityOfElement(WebNavigateUtil.createXPathBy(HTML_DIV_TYPE, WEB_ELEMENT_CRITERIA_FILTER_TABLE_ATTR_NAME, WEB_ELEMENT_CRITERIA_FILTER_TABLE_ATTR_VALUE), WAIT_FOR_CRITERIA_FILTER_TABLE_TO_APPEAR.toMillis());
   }

   private void init(boolean dryRun, Supplier<Duration> duration2WaitUntilCourseBecomesBookable) {
      this.courseFilterHelper = new CourseFilterHelper(this.webNavigatorHelper);
      CourseBookerHelper courseBookerHelper = new CourseBookerHelper(this.webNavigatorHelper, dryRun);
      this.courseSelectHelper = new CourseSelectHelper(courseBookerHelper, aquabasileaLoginHelper, this.webNavigatorHelper, duration2WaitUntilCourseBecomesBookable, dryRun, this::navigate2CoursePage);
   }

   private static CourseBookingEndResult handleExceptionAndBuildResult(String courseName, ErrorHandler errorHandler, Exception e) {
      errorHandler.handleError(ErrorUtil.getErrorMsgWithException(e));
      return buildCourseBookingEndResult(courseName, errorHandler, e, COURSE_NOT_SELECTED_EXCEPTION_OCCURRED);
   }

   private static CourseBookingEndResult buildCourseBookingEndResult(String courseName, ErrorHandler errorHandler, Exception exception, CourseClickedResult courseClickedResult) {
      return CourseBookingEndResultBuilder.builder()
              .withCourseName(courseName)
              .withException(exception)
              .withErrors(errorHandler.getErrors())
              .withCourseClickedResult(courseClickedResult)
              .build();
   }
}
