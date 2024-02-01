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
import com.aquabasilea.web.navigate.AbstractAquabasileaWebNavigator;
import com.aquabasilea.web.util.ErrorUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.function.Supplier;

import static com.aquabasilea.web.bookcourse.impl.select.result.CourseClickedResult.COURSE_NOT_SELECTED_EXCEPTION_OCCURRED;
import static com.aquabasilea.web.constant.AquabasileaWebConst.*;

public class AquabasileaWebCourseBookerImpl extends AbstractAquabasileaWebNavigator implements AquabasileaWebCourseBooker {

   private static final Logger LOG = LoggerFactory.getLogger(AquabasileaWebCourseBookerImpl.class);
   private char[] userPassword4Retries; // After a login, the password array is reset. If a timeout occurs, we might need to re-logging -> restore pw
   private int timeOutRetries;
   private CourseSelectWithRetryHelper courseSelectWithRetryHelper;
   private CourseFilterHelper courseFilterHelper;

   public AquabasileaWebCourseBookerImpl(String userName, char[] userPassword, String propertiesName) {
      super(userName, userPassword, propertiesName);
      this.timeOutRetries = 4;
      this.userPassword4Retries = Arrays.copyOf(userPassword, userPassword.length);
   }

   /**
    * Creates an {@link AquabasileaWebCourseBookerImpl} for the given parameters
    * If <code>dryRun</code> is set to <code>true</code> a course is selected but not booked in order to test the implementation
    *
    * @param userName                                the username
    * @param userPassword                            the password of the user
    * @param dryRun                                  <code>true</code> if its a dry run or <code>false</code> if it's a real booking
    * @param duration2WaitUntilCourseBecomesBookable the {@link Duration} the {@link AquabasileaWebCourseBookerImpl} waits if a course
    *                                                is not yet bookable. After that {@link Duration} a booking or dry-run will fail
    * @param propertiesFile                          the properties-file which contains the configuration values
    * @return a  {@link AquabasileaWebCourseBookerImpl}
    */
   public static AquabasileaWebCourseBooker createAndInitAquabasileaWebNavigator(String userName, char[] userPassword,
                                                                                 boolean dryRun, Supplier<Duration> duration2WaitUntilCourseBecomesBookable,
                                                                                 String propertiesFile) {
      AquabasileaWebCourseBookerImpl aquabasileaWebNavigator = new AquabasileaWebCourseBookerImpl(userName, userPassword, propertiesFile);
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

   /**
    * Sets the {@link #userPassword4Retries} as the <code>userPassword</code> of this {@link com.aquabasilea.web.navigate.AbstractAquabasileaWebNavigator}
    * Also the {@link #userPassword4Retries} is cloned, so it remains immutable when the <code>userPassword</code> gets filled-up with zeros
    */
   private void setUserPassword() {
      super.setUserPassword(userPassword4Retries);
      this.userPassword4Retries = Arrays.copyOf(userPassword4Retries, userPassword4Retries.length);
   }

   @Override
   public void login() {
      if (isAlreadyLoggedIn()) {
         LOG.warn("Already logged in!");
      } else if (isUsernameInputFieldNotPresent()) {
         LOG.warn("Not already logged in but no username input found! Skip login..");
      } else {
         setUserPassword();
         super.login();
      }
   }

   private CourseBookingEndResult handleTimeOutException(CourseBookDetails courseBookDetails, ErrorHandler errorHandler, TimeoutException e) {
      this.timeOutRetries--;
      String errorMsg = String.format("TimeoutException while selecting and booking the course '%s'. Retries left: %s", courseBookDetails.courseName(), timeOutRetries);
      logError(errorMsg, errorMsg, errorHandler, e);
      try {
         aquabasileaLoginHelper.tryClickLogoutButton();
      } catch (Exception ex) {
         LOG.error("Error while 'tryClickLogoutButton!", ex);
      }
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

   private void navigate2CoursePage() {
      navigate2CoursePageInternal(false);
   }

   private void init(boolean dryRun, Supplier<Duration> duration2WaitUntilCourseBecomesBookable) {
      this.courseFilterHelper = new CourseFilterHelper(this.webNavigatorHelper);
      Duration waitForCourseTableToAppear = AquabasileaWebConst.getWaitForCourseTableToAppearDuration(propertyReader);
      Duration pageRefreshDuration = AquabasileaWebConst.getPageRefreshDuration(propertyReader);
      Duration waitForBookDialogToAppear = AquabasileaWebConst.getWaitForBookDialogToAppearDuration(propertyReader);
      CourseBookerHelper courseBookerHelper = new CourseBookerHelper(this.webNavigatorHelper, dryRun);
      CourseSelectHelper courseSelectHelper = new CourseSelectHelper(courseBookerHelper, this.webNavigatorHelper, duration2WaitUntilCourseBecomesBookable, dryRun, waitForCourseTableToAppear, waitForBookDialogToAppear);
      this.courseSelectWithRetryHelper = new CourseSelectWithRetryHelper(courseSelectHelper, this.courseFilterHelper, this::navigate2CoursePage, duration2WaitUntilCourseBecomesBookable, pageRefreshDuration);
   }

   private CourseBookingEndResult handleExceptionAndBuildResult(String courseName, ErrorHandler errorHandler, Exception e) {
      logError(ErrorUtil.getErrorMsgWithException(e), "Error during course booking!", errorHandler, e);
      return buildCourseBookingEndResult(courseName, errorHandler, e, COURSE_NOT_SELECTED_EXCEPTION_OCCURRED);
   }

   private CourseBookingEndResult buildCourseBookingEndResult(String courseName, ErrorHandler errorHandler, Exception exception, CourseClickedResult courseClickedResult) {
      Arrays.fill(userPassword4Retries, '0');
      return CourseBookingEndResultBuilder.builder()
              .withCourseName(courseName)
              .withException(exception)
              .withErrors(errorHandler.getErrors())
              .withCourseClickedResult(courseClickedResult)
              .build();
   }

   private void logError(String errorMsg, String logErrorMsg, ErrorHandler errorHandler, Exception e) {
      errorHandler.handleError(errorMsg);
      LOG.error(logErrorMsg, e);
      webNavigatorHelper.takeScreenshot(e.getClass().getSimpleName());
   }

   private boolean isAlreadyLoggedIn() {
      return webNavigatorHelper.findWebElementById(ABMELDEN_BUTTON_ID)
              .isPresent();
   }

   private boolean isUsernameInputFieldNotPresent() {
      return this.webNavigatorHelper.findWebElementBy(null, By.id(this.getUserNameInputFieldId()))
              .isEmpty();
   }
}
