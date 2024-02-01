package com.aquabasilea.web.login;

import com.aquabasilea.web.error.ErrorHandlerImpl;
import com.aquabasilea.web.navigate.AquabasileaNavigatorHelper;
import com.zeiterfassung.web.common.impl.navigate.button.ButtonClickHelper;
import com.zeiterfassung.web.common.navigate.util.WebNavigateUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Optional;

import static com.aquabasilea.web.constant.AquabasileaWebConst.*;
import static com.zeiterfassung.web.common.constant.BaseWebConst.*;

public class AquabasileaLoginHelper {

   private static final Logger LOG = LoggerFactory.getLogger(AquabasileaLoginHelper.class);
   private final LoginCallback loginCallback;
   private final Duration waitForCourseTableToAppear;
   private final AquabasileaNavigatorHelper aquabasileaNavigatorHelper;
   private final ButtonClickHelper buttonClickHelper;

   public AquabasileaLoginHelper(AquabasileaNavigatorHelper aquabasileaNavigatorHelper, LoginCallback loginCallback,
                                 Duration waitForCourseTableToAppear) {
      this.aquabasileaNavigatorHelper = aquabasileaNavigatorHelper;
      this.buttonClickHelper = new ButtonClickHelper(aquabasileaNavigatorHelper);
      this.loginCallback = loginCallback;
      this.waitForCourseTableToAppear = waitForCourseTableToAppear;
   }

   /**
    * Does the login process, triggered when clicking the given login-button
    *
    * @param loginButton the login-button as {@link WebElement}
    */
   public void login(WebElement loginButton) {
      // Since there is an overlay placed over the button, selenium can't click the button directly
      aquabasileaNavigatorHelper.executeClickButtonScript(loginButton);
      aquabasileaNavigatorHelper.waitForVisibilityOfElement(By.id(WEB_ELEMENT_USER_NAME_FIELD_ID), Duration.ofMillis(2000));
      loginCallback.login();
      aquabasileaNavigatorHelper.waitForVisibilityOfElement(WebNavigateUtil.createXPathBy(HTML_DIV_TYPE, WEB_ELEMENT_CRITERIA_FILTER_TABLE_ATTR_NAME, WEB_ELEMENT_CRITERIA_FILTER_TABLE_ATTR_VALUE), Duration.ofMillis(25000));
      LOG.info("Login successful");
   }

   /**
    * Small hack: After the user is logged in, using the login page and returned to the booking page, we have to click
    * the 'Jetzt einloggen' Button in order to make the previous login effective.
    * If a {@link TimeoutException} occurs, and we first log out and try to log in again. Sometimes a logout is not possible (since something is blocking the button)
    * In that case, a login is not necessary and also this click on the login-button
    */
   public void navigateMemberareaAndClickLoginButton() {
      LOG.info("Try Login before navigating to course page");
      ErrorHandlerImpl errorHandler = new ErrorHandlerImpl();
      navigateToMemberarea(errorHandler);
      clickLoginButton(errorHandler);
   }

   private void clickLoginButton(ErrorHandlerImpl errorHandler) {
      By anmeldenBy = By.xpath("//button[contains(text(), 'Anmelden')]");
      this.aquabasileaNavigatorHelper.waitForVisibilityOfElement(anmeldenBy, waitForCourseTableToAppear);
      Optional<WebElement> anmeldenButtonOpt = this.aquabasileaNavigatorHelper.findWebElementBy(null, anmeldenBy);
      buttonClickHelper.clickButtonOrHandleErrorRecursively(() -> anmeldenButtonOpt, errorHandler::handleElementNotFound, "Anmelden-Button", 1);

      this.aquabasileaNavigatorHelper.waitForVisibilityOfElement(By.linkText("Memberbereich"), waitForCourseTableToAppear);
   }

   /*
    * Click on 'Memberbereich' so we navigate to the login area
    */
   private void navigateToMemberarea(ErrorHandlerImpl errorHandler) {
      By memberareaBy = By.linkText("Memberbereich");
      this.aquabasileaNavigatorHelper.waitForVisibilityOfElement(memberareaBy, waitForCourseTableToAppear);
      Optional<WebElement> nowLogInButtonOpt = this.aquabasileaNavigatorHelper.findWebElementBy(null, memberareaBy);
      buttonClickHelper.clickButtonOrHandleErrorRecursively(() -> nowLogInButtonOpt, errorHandler::handleElementNotFound, "Memberbereich-Button", 1);
      this.aquabasileaNavigatorHelper.waitForVisibilityOfElement(memberareaBy, waitForCourseTableToAppear);
   }

   /**
    * After a {@link TimeoutException} bevor we can do a whole retry, we have to make sure we're logged out
    * If the original {@link TimeoutException} occurred during login, then we have no logout button.
    * Obviously a logout is not necessary in that case
    */
   public void tryClickLogoutButton() {
      LOG.info("Try logout..");
      Optional<WebElement> logoutButtonOpt = this.aquabasileaNavigatorHelper.findWebElementByNameTagNameAndValue(null, HTML_BUTTON_TYPE, WEB_ELEMENT_LOGIN_SELECT_COURSE_ANMELDE_BUTTON_ATTR_ID, WEB_ELEMENT_LOGIN_SELECT_COURSE_ABMELDEN_BUTTON_ATTR_ID_TEXT);
      logoutButtonOpt.ifPresent(logoutButton -> {
         logoutButton.click();
         aquabasileaNavigatorHelper.waitForInvisibilityOfElement(logoutButton);
         LOG.info("Logout button clicked");
      });
      logIfLogoutButtonIsAbsent(logoutButtonOpt.isEmpty());
   }

   private void logIfLogoutButtonIsAbsent(boolean isLogoutButtonNotAvailable) {
      if (isLogoutButtonNotAvailable) {
         LOG.warn("No logout button available!");
         aquabasileaNavigatorHelper.takeScreenshot("no_logout_possible");
      }
   }
}
