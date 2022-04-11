package com.aquabasilea.web.login;

import com.aquabasilea.web.navigate.AquabasileaWebNavigatorImpl;

import static com.aquabasilea.web.constant.AquabasileaWebConst.AQUABASILEA_WEB_KURS_BUCHER_PROPERTIES;
import static com.aquabasilea.web.constant.AquabasileaWebConst.LOGIN_FAILED_ERROR_MSG_ID;

public class AquabasileaLogin extends AquabasileaWebNavigatorImpl {

   public AquabasileaLogin(String userName, String userPassword, String propertiesName) {
      super(userName, userPassword, propertiesName);
   }

   public static void main(String[] args) {
      AquabasileaLogin.createAquabasileaLogin("asdf", "df").doLogin();
   }

   /**
    * Creates and prepares a new {@link AquabasileaLogin}
    *
    * @param userName     the username
    * @param userPassword the user-password
    * @return a new {@link AquabasileaLogin}
    */
   public static AquabasileaLogin createAquabasileaLogin(String userName, String userPassword) {
      AquabasileaLogin aquabasileaLogin = new AquabasileaLogin(userName, userPassword, AQUABASILEA_WEB_KURS_BUCHER_PROPERTIES);
      aquabasileaLogin.initWebDriver();
      return aquabasileaLogin;
   }

   /**
    * Tries a login.
    *
    * @return <code>true</code> if the login was successful or <code>false</code> if not
    */
   public boolean doLogin() {
      super.navigateToPageAndLogin();
      boolean isLoginSuccessful =  this.webNavigatorHelper.findWebElementById(LOGIN_FAILED_ERROR_MSG_ID)
              .map(webElement -> false)
              .orElse(true);
      logout();
      return isLoginSuccessful;
   }
}
