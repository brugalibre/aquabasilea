/**
 * 
 */
package com.common.ui.login.auth.view;

import com.common.ui.core.view.impl.AbstractFXPage;
import com.common.ui.login.auth.control.LoginCallbackHandler;
import com.common.ui.login.auth.control.LoginController;
import com.common.ui.login.auth.model.LoginPageModel;
import javafx.stage.Stage;

import java.util.function.BiFunction;

/**
 * @author Dominic
 *
 */
public class LoginPage extends AbstractFXPage<LoginPageModel, LoginPageModel> {

   public LoginPage(LoginCallbackHandler userLogedInCallbackHandler,
                    BiFunction<String, String, Boolean> loginFunction, Stage primaryStage) {
      super(primaryStage, true);
      ((LoginController) getController()).setCallbackHandler(userLogedInCallbackHandler);
      ((LoginController) getController()).setLoginFunction(loginFunction);
   }
}
