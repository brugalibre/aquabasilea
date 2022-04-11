package com.common.ui.login.auth.control;

import com.common.ui.login.service.LoginResult;

/**
 * Callback handler invoked by the {@link LoginController} as soon as the login is done or aborted
 */
public interface LoginCallbackHandler {
   /**
    * Login was aborted by the user
    */
   void onLoginAborted();

   /**
    * Login was done by the user
    *
    * @param loginResult the {@link LoginResult}
    */
   void onLoginFinished(LoginResult loginResult);

   /**
    * Called when an exception occurred
    *
    * @param throwable the thrown {@link Throwable} which was thrown
    */
   void onLoginError(Throwable throwable);
}
