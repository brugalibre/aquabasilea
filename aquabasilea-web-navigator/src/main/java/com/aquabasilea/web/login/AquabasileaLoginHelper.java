package com.aquabasilea.web.login;

import com.aquabasilea.web.navigate.AquabasileaNavigatorHelper;
import com.zeiterfassung.web.common.navigate.util.WebNavigateUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static com.aquabasilea.web.constant.AquabasileaWebConst.*;
import static com.zeiterfassung.web.common.constant.BaseWebConst.HTML_BUTTON_TYPE;
import static com.zeiterfassung.web.common.constant.BaseWebConst.HTML_DIV_TYPE;

public class AquabasileaLoginHelper {

   private final LoginCallback loginCallback;
   private AquabasileaNavigatorHelper aquabasileaNavigatorHelper;

   public AquabasileaLoginHelper(AquabasileaNavigatorHelper aquabasileaNavigatorHelper, LoginCallback loginCallback) {
      this.aquabasileaNavigatorHelper = aquabasileaNavigatorHelper;
      this.loginCallback = loginCallback;
   }

   /**
    * Does the login process, triggered when clicking the given login-button
    *
    * @param loginButton the login-button as {@link WebElement}
    */
   public void login(WebElement loginButton) {
      // Since there is an overlay placed over the button, selenium can't click the button directly
      aquabasileaNavigatorHelper.executeClickButtonScript(loginButton);
      aquabasileaNavigatorHelper.waitForVisibilityOfElement(By.id(WEB_ELEMENT_USER_NAME_FIELD_ID), 2000);
      loginCallback.login();
      aquabasileaNavigatorHelper.waitForVisibilityOfElement(WebNavigateUtil.createXPathBy(HTML_DIV_TYPE, WEB_ELEMENT_CRITERIA_FILTER_TABLE_ATTR_NAME, WEB_ELEMENT_CRITERIA_FILTER_TABLE_ATTR_VALUE), 25000);
   }

   /**
    * Small hack: After the user is logged in, using the login page and returned to the booking page, we have to click
    * the 'Jetzt einloggen' Button in order to make the previous login effective
    */
   public void clickLoginButton() {
      aquabasileaNavigatorHelper.waitForVisibilityOfElement(WebNavigateUtil.createXPathBy(HTML_BUTTON_TYPE, WEB_ELEMENT_LOGIN_SELECT_COURSE_ANMELDE_BUTTON_ATTR_ID, WEB_ELEMENT_LOGIN_SELECT_COURSE_ANMELDE_BUTTON_ATTR_ID_TEXT), 20000);
      WebElement nowLogInButton = this.aquabasileaNavigatorHelper.getWebElementByNameTagNameAndValue(null, HTML_BUTTON_TYPE, WEB_ELEMENT_LOGIN_SELECT_COURSE_ANMELDE_BUTTON_ATTR_ID, WEB_ELEMENT_LOGIN_SELECT_COURSE_ANMELDE_BUTTON_ATTR_ID_TEXT);
      nowLogInButton.click();
      aquabasileaNavigatorHelper.waitForInvisibilityOfElement(nowLogInButton);
   }
}
