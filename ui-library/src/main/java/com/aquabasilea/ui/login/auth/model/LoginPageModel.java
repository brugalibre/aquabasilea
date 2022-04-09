package com.aquabasilea.ui.login.auth.model;

import com.aquabasilea.ui.core.model.PageModel;
import com.aquabasilea.ui.i18n.TextResources;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;

public class LoginPageModel implements PageModel {
   private Property<String> userNameFieldProperty;
   private Property<String> loginButtonProperty;
   private Property<String> userPwdFieldPromptProperty;
   private Property<String> userNameFieldPromptProperty;

   public LoginPageModel() {
      userNameFieldProperty = new SimpleStringProperty(System.getProperty("user.name"));// Yes this can be spoofed, but at least we see it on the ui
      userNameFieldPromptProperty = new SimpleStringProperty(TextResources.USERNAME_FIELD_PROMPT);
      loginButtonProperty = new SimpleStringProperty(TextResources.LOGIN_LABEL);
      userPwdFieldPromptProperty = new SimpleStringProperty(TextResources.USER_PW_FIELD_PROMPT);
   }

   public Property<String> getUserNameFieldProperty() {
      return userNameFieldProperty;
   }

   public Property<String> getLoginButtonProperty() {
      return loginButtonProperty;
   }

   public String getUsername() {
      return userNameFieldProperty.getValue();
   }

   public Property<String> getUserNameFieldPrompProperty() {
      return userNameFieldPromptProperty;
   }

   public Property<String> getUserPwdFieldPromptProperty() {
      return userPwdFieldPromptProperty;
   }
}
