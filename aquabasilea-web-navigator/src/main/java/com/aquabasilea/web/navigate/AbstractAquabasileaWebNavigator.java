package com.aquabasilea.web.navigate;

import com.aquabasilea.web.constant.AquabasileaWebConst;
import com.aquabasilea.web.login.AquabasileaLoginHelper;
import com.zeiterfassung.web.common.impl.navigate.BaseWebNavigator;
import com.zeiterfassung.web.common.inout.PropertyReader;
import com.zeiterfassung.web.common.navigate.util.WebNavigateUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;

import static com.aquabasilea.web.constant.AquabasileaWebConst.*;
import static com.aquabasilea.web.constant.AquabasileaWebConst.WAIT_FOR_CRITERIA_FILTER_TABLE_TO_APPEAR;
import static com.zeiterfassung.web.common.constant.BaseWebConst.HTML_BUTTON_TYPE;
import static com.zeiterfassung.web.common.constant.BaseWebConst.HTML_DIV_TYPE;

public abstract class AbstractAquabasileaWebNavigator extends BaseWebNavigator<AquabasileaNavigatorHelper> {

   private final Duration waitForCourseTableToAppear;
   private final Duration durationUntilLoadingAnimationDisappears;
   protected final PropertyReader propertyReader;
   protected AquabasileaLoginHelper aquabasileaLoginHelper;

   public AbstractAquabasileaWebNavigator(String userName, char[] userPassword, String propertiesName) {
      super(userName, userPassword, propertiesName);
      this.propertyReader = new PropertyReader(propertiesName);
      this.durationUntilLoadingAnimationDisappears = AquabasileaWebConst.getWaitUntilLoadingAnimationDisappearsDuration(propertyReader);
      this.waitForCourseTableToAppear = AquabasileaWebConst.getWaitForCourseTableToAppearDuration(propertyReader);
   }

   @Override
   protected AquabasileaNavigatorHelper createWebNavigatorHelper(WebDriver webDriver) {
      return new AquabasileaNavigatorHelper(webDriver, durationUntilLoadingAnimationDisappears);
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

   public void login() {
      this.enterUserName(true);
      webNavigatorHelper.waitForVisibilityOfElement(By.id(this.getUserPasswordInputFieldId()), Duration.ofSeconds(3));
      this.enterUserPassword(true);
   }

   protected void navigate2CoursePageInternal(boolean clickLoginButton) {
      String coursePage = propertyReader.readValue(COURSE_PAGE);
      navigateToPage(coursePage);
      if (clickLoginButton) {
         aquabasileaLoginHelper.navigateMemberareaAndClickLoginButton();
      }
      wait4Navigate2CoursePageCompleted();
   }

   protected void wait4Navigate2CoursePageCompleted() {
      // yes, this may take a veeeeeeeeeeeeery long time
      waitForVisibilityOfElement(WebNavigateUtil.createXPathBy(HTML_DIV_TYPE, WEB_ELEMENT_CRITERIA_FILTER_TABLE_ATTR_NAME, WEB_ELEMENT_CRITERIA_FILTER_TABLE_ATTR_VALUE), WAIT_FOR_CRITERIA_FILTER_TABLE_TO_APPEAR.toMillis());
   }

   @Override
   public void initWebDriver() {
      super.initWebDriver();
      this.aquabasileaLoginHelper = new AquabasileaLoginHelper(this.webNavigatorHelper, this::login, waitForCourseTableToAppear);
   }
}
