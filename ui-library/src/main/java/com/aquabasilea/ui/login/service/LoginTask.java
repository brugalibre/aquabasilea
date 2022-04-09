package com.aquabasilea.ui.login.service;

import javafx.concurrent.Task;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public class LoginTask extends Task<LoginResult> {

   private BiFunction<String, String, Boolean> loginFunction;
   private Supplier<String> usernameSupplier;
   private Supplier<char[]> userPwdSupplier;

   /**
    * Creates a new {@link LoginTask}
    *
    * @param usernameSupplier the {@link Supplier} for the user name
    * @param userPwdSupplier  the {@link Supplier} for the user password
    * @param loginFunction    the actual login logic
    */
   public LoginTask(Supplier<String> usernameSupplier, Supplier<char[]> userPwdSupplier,
                    BiFunction<String, String, Boolean> loginFunction) {
      this.usernameSupplier = requireNonNull(usernameSupplier);
      this.userPwdSupplier = requireNonNull(userPwdSupplier);
      this.loginFunction = loginFunction;
   }

   @Override
   protected LoginResult call() {
      Boolean loginResult = loginFunction.apply(usernameSupplier.get(), String.valueOf(userPwdSupplier.get()));
      return new LoginResult(loginResult, usernameSupplier.get(), userPwdSupplier.get());
   }
}
