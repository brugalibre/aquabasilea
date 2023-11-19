package com.aquabasilea.web.login;

import com.aquabasilea.web.bookcourse.impl.AquabasileaWebCourseBookerImpl;

import static com.aquabasilea.web.constant.AquabasileaWebConst.*;

public class AquabasileaLogin extends AquabasileaWebCourseBookerImpl {

   public AquabasileaLogin(String userName, char[] userPassword, String propertiesName) {
      super(userName, userPassword, propertiesName);
   }

   public static void main(String[] args) {
      AquabasileaLogin.createAquabasileaLogin("asdf", new char[]{}, "config/aquabasilea-kurs-bucher-config.yml").doLogin();
   }

   /**
    * Creates and prepares a new {@link AquabasileaLogin}
    *
    * @param userName             the username
    * @param userPassword         the user-password
    * @param configPropertiesFile the properties file for the configuration
    * @return a new {@link AquabasileaLogin}
    */
   public static AquabasileaLogin createAquabasileaLogin(String userName, char[] userPassword, String configPropertiesFile) {
      AquabasileaLogin aquabasileaLogin = new AquabasileaLogin(userName, userPassword, configPropertiesFile);
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
      boolean isLoginSuccessful = this.webNavigatorHelper.findWebElementById(LOGIN_FAILED_ERROR_MSG_ID)
              .isEmpty();
      navigate2CoursePageInternal(true);
      logout();
      return isLoginSuccessful;
   }
}
