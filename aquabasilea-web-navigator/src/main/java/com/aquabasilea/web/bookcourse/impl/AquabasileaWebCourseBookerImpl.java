package com.aquabasilea.web.bookcourse.impl;

import com.aquabasilea.web.bookcourse.AquabasileaWebCourseBooker;
import com.aquabasilea.web.bookcourse.impl.book.CourseBookerHelper;
import com.aquabasilea.web.bookcourse.impl.book.CourseSelectWithRetryHelper;
import com.aquabasilea.web.bookcourse.impl.select.CourseSelectHelper;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseBookingEndResult;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseBookingEndResult.CourseBookingEndResultBuilder;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseClickedResult;
import com.aquabasilea.web.bookcourse.model.CourseBookDetails;
import com.aquabasilea.web.constant.AquabasileaWebConst;
import com.aquabasilea.web.error.ErrorHandler;
import com.aquabasilea.web.error.ErrorHandlerImpl;
import com.aquabasilea.web.filtercourse.CourseFilterHelper;
import com.aquabasilea.web.login.AquabasileaLoginHelper;
import com.aquabasilea.web.navigate.AquabasileaNavigatorHelper;
import com.aquabasilea.web.util.ErrorUtil;
import com.zeiterfassung.web.common.impl.navigate.BaseWebNavigator;
import com.zeiterfassung.web.common.inout.PropertyReader;
import com.zeiterfassung.web.common.navigate.util.WebNavigateUtil;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.function.Supplier;

import static com.aquabasilea.web.bookcourse.impl.select.result.CourseClickedResult.COURSE_NOT_SELECTED_EXCEPTION_OCCURRED;
import static com.aquabasilea.web.constant.AquabasileaWebConst.*;
import static com.zeiterfassung.web.common.constant.BaseWebConst.HTML_BUTTON_TYPE;
import static com.zeiterfassung.web.common.constant.BaseWebConst.HTML_DIV_TYPE;

public class AquabasileaWebCourseBookerImpl extends BaseWebNavigator<AquabasileaNavigatorHelper> implements AquabasileaWebCourseBooker {

   private static final Logger LOG = LoggerFactory.getLogger(AquabasileaWebCourseBookerImpl.class);
   private int timeOutRetries;
   private final String coursePage;
   private CourseSelectWithRetryHelper courseSelectWithRetryHelper;
   private CourseFilterHelper courseFilterHelper;
   private AquabasileaLoginHelper aquabasileaLoginHelper;

   public AquabasileaWebCourseBookerImpl(String userName, String userPassword, String propertiesName) {
      super(userName, userPassword, propertiesName);
      PropertyReader propertyReader = new PropertyReader(propertiesName);
      this.coursePage = propertyReader.readValue(COURSE_PAGE);
      this.timeOutRetries = 4;
   }

   public static AquabasileaWebCourseBooker createAndInitAquabasileaWebNavigator(String userName, String userPassword, boolean dryRun, Supplier<Duration> duration2WaitUntilCourseBecomesBookable) {
      AquabasileaWebCourseBookerImpl aquabasileaWebNavigator = new AquabasileaWebCourseBookerImpl(userName, userPassword, AQUABASILEA_WEB_KURS_BUCHER_PROPERTIES);
      aquabasileaWebNavigator.initWebDriver();
      aquabasileaWebNavigator.init(dryRun, duration2WaitUntilCourseBecomesBookable);
      return aquabasileaWebNavigator;
   }

   @Override
   public CourseBookingEndResult selectAndBookCourse(CourseBookDetails courseBookDetails) {
      ErrorHandler errorHandler = new ErrorHandlerImpl();
      try {
         return selectAndBookCourse(courseBookDetails, errorHandler);
      } catch (TimeoutException e) {
         if (timeOutRetries > 0) {
            return handleTimeOutException(courseBookDetails, errorHandler, e);
         }
         return handleExceptionAndBuildResult(courseBookDetails.courseName(), errorHandler, e);
      } catch (Exception e) {
         return handleExceptionAndBuildResult(courseBookDetails.courseName(), errorHandler, e);
      }
   }

   private CourseBookingEndResult handleTimeOutException(CourseBookDetails courseBookDetails, ErrorHandler errorHandler, TimeoutException e) {
      this.timeOutRetries--;
      String errorMsg = String.format("TimeoutException while selecting and booking the course '%s'. Retries left: %s", courseBookDetails.courseName(), timeOutRetries);
      logError(errorMsg, errorMsg, errorHandler, e);
      aquabasileaLoginHelper.clickLogoutButton();
      return selectAndBookCourse(courseBookDetails);
   }

   private CourseBookingEndResult selectAndBookCourse(CourseBookDetails courseBookDetails, ErrorHandler errorHandler) {
      navigateToPageAndLogin();
      navigate2CoursePageInternal(true);
      courseFilterHelper.applyCriteriaFilter(courseBookDetails, errorHandler);
      CourseClickedResult courseClickedResult = courseSelectWithRetryHelper.selectAndBookCourseWithRetry(courseBookDetails, errorHandler);
      logout();
      return buildCourseBookingEndResult(courseBookDetails.courseName(), errorHandler, null, courseClickedResult);
   }

   @Override
   protected AquabasileaNavigatorHelper createWebNavigatorHelper(WebDriver webDriver) {
      return new AquabasileaNavigatorHelper(webDriver);
   }

   @Override
   protected WebElement findLoginSubmitButton() {
      return this.webNavigatorHelper.getWebElementByTageNameAndInnerHtmlValue(null, HTML_BUTTON_TYPE, AquabasileaWebConst.WEB_ELEMENT_ANMELDE_BUTTON_TEXT);
   }

   @Override
   protected String getUserPasswordInputFieldId() {
      return AquabasileaWebConst.WEB_ELEMENT_PWD_FIELD_ID;
   }

   @Override
   protected String getUserNameInputFieldId() {
      return AquabasileaWebConst.WEB_ELEMENT_USER_NAME_FIELD_ID;
   }

   @Override
   protected String getLoginSubmitButtonId() {
      return null; // Submitbutton unfortunately doesn't have an id
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

   @Override
   public void initWebDriver() {
      super.initWebDriver();
      this.aquabasileaLoginHelper = new AquabasileaLoginHelper(this.webNavigatorHelper, this::login);
   }

   private void init(boolean dryRun, Supplier<Duration> duration2WaitUntilCourseBecomesBookable) {
      this.courseFilterHelper = new CourseFilterHelper(this.webNavigatorHelper);
      CourseBookerHelper courseBookerHelper = new CourseBookerHelper(this.webNavigatorHelper, dryRun);
      CourseSelectHelper courseSelectHelper = new CourseSelectHelper(courseBookerHelper, aquabasileaLoginHelper, this.webNavigatorHelper, duration2WaitUntilCourseBecomesBookable, dryRun);
      this.courseSelectWithRetryHelper = new CourseSelectWithRetryHelper(courseSelectHelper, this.courseFilterHelper, this::navigate2CoursePage, duration2WaitUntilCourseBecomesBookable);
   }

   private CourseBookingEndResult handleExceptionAndBuildResult(String courseName, ErrorHandler errorHandler, Exception e) {
      logError(ErrorUtil.getErrorMsgWithException(e), "Error during course booking!", errorHandler, e);
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

   private void logError(String errorMsg,String logErrorMsg, ErrorHandler errorHandler, Exception e) {
      errorHandler.handleError(errorMsg);
      LOG.error(logErrorMsg, e);
      webNavigatorHelper.takeScreenshot(e.getClass().getSimpleName());
   }
}
