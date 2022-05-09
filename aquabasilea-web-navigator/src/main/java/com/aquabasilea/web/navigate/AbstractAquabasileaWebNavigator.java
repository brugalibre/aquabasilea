package com.aquabasilea.web.navigate;

import com.aquabasilea.web.constant.AquabasileaWebConst;
import com.aquabasilea.web.login.AquabasileaLoginHelper;
import com.zeiterfassung.web.common.impl.navigate.BaseWebNavigator;
import com.zeiterfassung.web.common.inout.PropertyReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.aquabasilea.web.constant.AquabasileaWebConst.COURSE_PAGE;
import static com.zeiterfassung.web.common.constant.BaseWebConst.HTML_BUTTON_TYPE;

public abstract class AbstractAquabasileaWebNavigator extends BaseWebNavigator<AquabasileaNavigatorHelper> {

   protected final String coursePage;
   protected AquabasileaLoginHelper aquabasileaLoginHelper;

   public AbstractAquabasileaWebNavigator(String userName, String userPassword, String propertiesName) {
      super(userName, userPassword, propertiesName);
      PropertyReader propertyReader = new PropertyReader(propertiesName);
      this.coursePage = propertyReader.readValue(COURSE_PAGE);
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

   @Override
   public void initWebDriver() {
      super.initWebDriver();
      this.aquabasileaLoginHelper = new AquabasileaLoginHelper(this.webNavigatorHelper, this::login);
   }
}
