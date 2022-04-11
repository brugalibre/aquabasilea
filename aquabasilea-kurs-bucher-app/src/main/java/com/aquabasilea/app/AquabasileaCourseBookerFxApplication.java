package com.aquabasilea.app;

import com.aquabasilea.app.style.LookAndFeel;
import com.aquabasilea.app.systemtray.AquabasileaCourseBookerApplication;
import com.aquabasilea.app.systemtray.icons.ImageLibrary;
import com.aquabasilea.coursebooker.AquabasileaCourseBooker;
import com.aquabasilea.web.login.AquabasileaLogin;
import com.common.ui.login.auth.control.LoginCallbackHandler;
import com.common.ui.login.auth.view.LoginPage;
import com.common.ui.login.service.LoginResult;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiFunction;

public class AquabasileaCourseBookerFxApplication extends Application implements LoginCallbackHandler {

   private static final Logger LOG = LoggerFactory.getLogger(AquabasileaCourseBookerFxApplication.class);
   private AquabasileaCourseBooker aquabasileaCourseBooker;

   @Override
   public void start(Stage primaryStage) {
      ImageLibrary.loadPictures();
      LookAndFeel.setNimbusLookAndFeel();
      this.aquabasileaCourseBooker = AquabasileaCourseBookerApplication.createAquabasileaCourseBookerAndSystemTray("user", "pwd");
      login(this, new Stage());
      aquabasileaCourseBooker.run();
   }

   private static void login(LoginCallbackHandler loginCallbackHandler, Stage primaryStage) {
      new LoginPage(loginCallbackHandler, getAquabasileaLoginFunction(), primaryStage).show();
   }

   @Override
   public void stop() throws Exception {
      super.stop();
      System.exit(0);
   }

   @Override
   public void onLoginAborted() {
      Platform.exit();
   }

   @Override
   public void onLoginFinished(LoginResult loginResult) {
      if (loginResult.isSuccess()) {
         aquabasileaCourseBooker.onUserAuthenticated(loginResult.getUsername(), loginResult::getUserPwd);
      }
   }

   @Override
   public void onLoginError(Throwable throwable) {
      LOG.error("Error during login!", throwable);
   }

   private static BiFunction<String, String, Boolean> getAquabasileaLoginFunction() {
      return (username, password) -> {
         AquabasileaLogin aquabasileaLogin = AquabasileaLogin.createAquabasileaLogin(username, password);
         return aquabasileaLogin.doLogin();
      };
   }
}
