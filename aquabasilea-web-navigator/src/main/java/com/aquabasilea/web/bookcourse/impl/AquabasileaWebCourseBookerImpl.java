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
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

import static com.aquabasilea.web.bookcourse.impl.select.result.CourseClickedResult.COURSE_NOT_SELECTED_EXCEPTION_OCCURRED;
import static com.aquabasilea.web.constant.AquabasileaWebConst.*;
import static com.zeiterfassung.web.common.constant.BaseWebConst.HTML_BUTTON_TYPE;
import static com.zeiterfassung.web.common.constant.BaseWebConst.HTML_DIV_TYPE;

public class AquabasileaWebCourseBookerImpl extends BaseWebNavigator<AquabasileaNavigatorHelper> implements AquabasileaWebCourseBooker {

   private static final Logger LOG = LoggerFactory.getLogger(AquabasileaWebCourseBookerImpl.class);
   private char[] userPassword4Retries; // After a login, the password array is reset. If a timeout occurs, we might need to re-logging -> restore pw
   private int timeOutRetries;
   private final String coursePage;
   private CourseSelectWithRetryHelper courseSelectWithRetryHelper;
   private CourseFilterHelper courseFilterHelper;
   private AquabasileaLoginHelper aquabasileaLoginHelper;

   public AquabasileaWebCourseBookerImpl(String userName, char[] userPassword, String propertiesName) {
      super(userName, userPassword, propertiesName);
      PropertyReader propertyReader = new PropertyReader(propertiesName);
      this.coursePage = propertyReader.readValue(COURSE_PAGE);
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
    * @return a  {@link AquabasileaWebCourseBookerImpl}
    */
   public static AquabasileaWebCourseBooker createAndInitAquabasileaWebNavigator(String userName, char[] userPassword, boolean dryRun, Supplier<Duration> duration2WaitUntilCourseBecomesBookable) {
      return createAndInitAquabasileaWebNavigator(userName, userPassword, dryRun, duration2WaitUntilCourseBecomesBookable, AQUABASILEA_WEB_KURS_BUCHER_PROPERTIES);
   }

   public static AquabasileaWebCourseBooker createAndInitAquabasileaWebNavigator(String userName, char[] userPassword, boolean dryRun, Supplier<Duration> duration2WaitUntilCourseBecomesBookable, String propertiesFile) {
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
      CourseSelectHelper courseSelectHelper = new CourseSelectHelper(courseBookerHelper, this.webNavigatorHelper, duration2WaitUntilCourseBecomesBookable, dryRun);
      this.courseSelectWithRetryHelper = new CourseSelectWithRetryHelper(courseSelectHelper, this.courseFilterHelper, this::navigate2CoursePage, duration2WaitUntilCourseBecomesBookable);
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
      Optional<WebElement> accountLoginIconOpt = webNavigatorHelper.findWebElementBy(null, By.className(MIGROS_ACOUNT_LOGIN_ICON));
      Optional<WebElement> accountTitleProfileLinkOpt = webNavigatorHelper.findWebElementBy(null, By.className(MIGROS_ACCOUNT_TILE_PROFILE_LINK));
      return accountLoginIconOpt.isPresent() || accountTitleProfileLinkOpt.isPresent();
   }

   private boolean isUsernameInputFieldNotPresent() {
      return this.webNavigatorHelper.findWebElementBy(null, By.id(this.getUserNameInputFieldId()))
              .isEmpty();
   }
}
