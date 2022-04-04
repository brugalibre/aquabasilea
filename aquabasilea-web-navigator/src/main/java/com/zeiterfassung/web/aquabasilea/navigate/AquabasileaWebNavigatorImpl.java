package com.zeiterfassung.web.aquabasilea.navigate;

import com.zeiterfassung.web.aquabasilea.bookcourse.CourseBookerHelper;
import com.zeiterfassung.web.aquabasilea.constant.AquabasileaWebConst;
import com.zeiterfassung.web.aquabasilea.error.ErrorHandler;
import com.zeiterfassung.web.aquabasilea.error.ErrorHandlerImpl;
import com.zeiterfassung.web.aquabasilea.filtercourse.CourseFilterHelper;
import com.zeiterfassung.web.aquabasilea.login.AquabasileaLoginHelper;
import com.zeiterfassung.web.aquabasilea.selectcourse.CourseSelectHelper;
import com.zeiterfassung.web.aquabasilea.selectcourse.result.CourseBookingEndResult;
import com.zeiterfassung.web.aquabasilea.selectcourse.result.CourseBookingEndResult.CourseBookingEndResultBuilder;
import com.zeiterfassung.web.aquabasilea.selectcourse.result.CourseClickedResult;
import com.zeiterfassung.web.aquabasilea.util.ErrorUtil;
import com.zeiterfassung.web.common.impl.navigate.BaseWebNavigator;
import com.zeiterfassung.web.common.inout.PropertyReader;
import com.zeiterfassung.web.common.navigate.util.WebNavigateUtil;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;

import static com.zeiterfassung.web.aquabasilea.constant.AquabasileaWebConst.*;
import static com.zeiterfassung.web.aquabasilea.selectcourse.result.CourseClickedResult.COURSE_NOT_SELECTED_EXCEPTION_OCCURRED;
import static com.zeiterfassung.web.common.constant.BaseWebConst.HTML_BUTTON_TYPE;
import static com.zeiterfassung.web.common.constant.BaseWebConst.HTML_DIV_TYPE;

public class AquabasileaWebNavigatorImpl extends BaseWebNavigator<AquabasileaNavigatorHelper> implements AquabasileaWebNavigator {

   private final static Logger LOG = LoggerFactory.getLogger(AquabasileaWebNavigatorImpl.class);
   private final String coursePage;
   private CourseSelectHelper courseSelectHelper;
   private CourseFilterHelper courseFilterHelper;
   private CourseBookerHelper courseBookerHelper;
   private AquabasileaLoginHelper aquabasileaLoginHelper;

   private AquabasileaWebNavigatorImpl(String userName, String userPassword, String propertiesName) {
      super(userName, userPassword, propertiesName);
      PropertyReader propertyReader = new PropertyReader(propertiesName);
      this.coursePage = propertyReader.readValue(COURSE_PAGE);
   }

   public static AquabasileaWebNavigator createAndInitAquabasileaWebNavigator(String userName, String userPassword, boolean dryRun, LongSupplier time2WaitUntilCourseBecomesBookable) {
      AquabasileaWebNavigatorImpl aquabasileaWebNavigator = new AquabasileaWebNavigatorImpl(userName, userPassword, AQUABASILEA_WEB_KURS_BUCHER_PROPERTIES);
      aquabasileaWebNavigator.initWebDriver();
      aquabasileaWebNavigator.init(dryRun, time2WaitUntilCourseBecomesBookable);
      return aquabasileaWebNavigator;
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
      navigate2CoursePageInternal(true);
      courseFilterHelper.applyCriteriaFilter(courseName, dayOfWeek, errorHandler);
      CourseClickedResult courseClickedResult = courseSelectHelper.selectCourseAndBook(courseName, errorHandler);
      logout();
      return buildCourseBookingEndResult(courseName, errorHandler, null, courseClickedResult);
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

   private void init(boolean dryRun, LongSupplier time2WaitUntilCourseBecomesBookable) {
      this.aquabasileaLoginHelper = new AquabasileaLoginHelper(this.webNavigatorHelper, this::login);
      this.courseFilterHelper = new CourseFilterHelper(this.webNavigatorHelper);
      this.courseBookerHelper = new CourseBookerHelper(this.webNavigatorHelper, dryRun);
      this.courseSelectHelper = new CourseSelectHelper(this.courseBookerHelper, aquabasileaLoginHelper, this.webNavigatorHelper, time2WaitUntilCourseBecomesBookable, dryRun, this::navigate2CoursePage);
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
