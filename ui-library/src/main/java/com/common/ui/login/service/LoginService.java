package com.common.ui.login.service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * The {@link LoginService} does the actually loging stuff like. It creates a {@link LoginTask}
 * which will be automatically called when calling {@link LoginService#login()}
 * 
 * @author dominic
 *
 */
public class LoginService extends Service<LoginResult> {
   private Supplier<String> usernameSupplier;
   private Supplier<char[]> userPwdSupplier;
   private BiFunction<String, String, Boolean> loginFunction;

   public LoginService(Supplier<String> usernameSupplier, Supplier<char[]> userPwdSupplier) {
      this.usernameSupplier = usernameSupplier;
      this.userPwdSupplier = userPwdSupplier;
   }

   /**
    * Does the actually login process.
    * This calls {@link javafx.concurrent.Service#restart}
    */
   public void login() {
      this.restart();
   }

   @Override
   protected Task<LoginResult> createTask() {
      return new LoginTask(usernameSupplier, userPwdSupplier, loginFunction);
   }

   public void setLoginFunction(BiFunction<String, String, Boolean> loginFunction) {
      this.loginFunction = loginFunction;
   }
}
