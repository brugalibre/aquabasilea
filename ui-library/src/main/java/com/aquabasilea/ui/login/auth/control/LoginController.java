/**
 *
 */

import com.aquabasilea.ui.core.control.impl.BaseFXController;
import com.aquabasilea.ui.core.model.resolver.PageModelResolver;
import com.aquabasilea.ui.core.view.Page;
import com.aquabasilea.ui.i18n.TextResources;
import com.aquabasilea.ui.login.auth.control.LoginCallbackHandler;
import com.aquabasilea.ui.login.auth.model.LoginPageModel;
import com.aquabasilea.ui.login.auth.model.resolver.LoginPageModelResolver;
import com.aquabasilea.ui.login.service.LoginResult;
import com.aquabasilea.ui.login.service.LoginService;
import com.aquabasilea.ui.util.ExceptionUtil;
import com.common.ui.constant.ImageConst;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.util.function.BiFunction;

import static java.util.Objects.*;

/**
 * @author Dominic
 *
 */
public class LoginController extends BaseFXController<LoginPageModel, LoginPageModel> {

   @FXML
   private VBox vBox;

   @FXML
   private ProgressIndicator progressIndicator;

   @FXML
   private TextField userNameField;

   @FXML
   private Label loginFailedLabel;

   @FXML
   private PasswordField userPwdField;

   @FXML
   private Button loginButton;

   private BiFunction<String, String, Boolean> loginFunction;
   private LoginService loginService;
   private LoginCallbackHandler loginCallbackHandler;

   @Override
   public void show(LoginPageModel dataModelIn) {
      // Since we set the username in the pagemodel already we travers the focus to the password field. Since show() is blocking, we have to do this first
      userPwdField.requestFocus();
      super.show(dataModelIn);
   }

   @Override
   public void hide() {
      super.hide();
      userPwdField.clear();
   }

   @Override
   public void initialize(Page<LoginPageModel, LoginPageModel> page) {
      createLoginService();
      super.initialize(page);
      prepareLoginStage();
      vBox.getChildren().remove(loginFailedLabel);
   }

   private void createLoginService() {
      this.loginService = new LoginService(() -> dataModel.getUsername(), this::getUserPwd, this.loginFunction);
      loginService.setOnSucceeded(onSucceededHandler());
      loginService.setOnFailed(onFailedHandler());
   }

   private EventHandler<WorkerStateEvent> onFailedHandler() {
      return workerStateEvent -> {
         Worker<?> worker = workerStateEvent.getSource();
         if (nonNull(worker.getException())) {
            ExceptionUtil.showException(Thread.currentThread(), worker.getException());
         } else {
            handleLoginFailed();
         }
         loginCallbackHandler.onLoginError(worker.getException());
      };
   }

   private EventHandler<WorkerStateEvent> onSucceededHandler() {
      return workerStateEvent -> {
         LoginResult loginResult = loginService.getValue();
         if (loginResult.isSuccess()) {
            hide();
         } else {
            handleLoginFailed();
         }
         loginCallbackHandler.onLoginFinished(loginResult);
      };
   }

   private void prepareLoginStage() {
      Stage stage = getStage();
      stage.setTitle(TextResources.LOGIN_LABEL);
      stage.getIcons().add(new Image(ImageConst.APP_LOGO_URL));
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.setResizable(false);
      stage.setAlwaysOnTop(true);
      stage.sizeToScene();
      stage.setOnCloseRequest(event -> {
         hide();
         loginCallbackHandler.onLoginAborted();
      });
   }

   @FXML
   private void login(ActionEvent actionEvent) {
      if (isValid()) {
         requireNonNull(loginFunction, "call setLoginFunction() first!");
         if (!loginService.isRunning()) {
            loginService.login();
         }
      } else {
         Toolkit.getDefaultToolkit().beep();
      }
   }

   private void handleLoginFailed() {
      Toolkit.getDefaultToolkit().beep();
      addLoginFailedLabel();
      loginFailedLabel.setText(TextResources.LOGIN_FAILED_LABEL);
      Stage stage = getStage();
      stage.sizeToScene();
   }

   private void addLoginFailedLabel() {
      if (!vBox.getChildren().contains(loginFailedLabel)) {
         vBox.getChildren().add(loginFailedLabel);
      }
   }

   private char[] getUserPwd() {
      return userPwdField.getText().toCharArray();
   }

   private boolean isValid() {
      if (isNull(userNameField.getText()) || userNameField.getText().length() == 0) {
         return false;
      }
      if (isNull(userPwdField.getText()) || userPwdField.getText().length() == 0) {
         return false;
      }
      return true;
   }

   @Override
   protected PageModelResolver<LoginPageModel, LoginPageModel> createPageModelResolver() {
      return new LoginPageModelResolver();
   }

   @Override
   protected void setBinding(LoginPageModel pageModel) {
      userNameField.textProperty().bindBidirectional(pageModel.getUserNameFieldProperty());
      userNameField.promptTextProperty().bindBidirectional(pageModel.getUserNameFieldPrompProperty());
      userPwdField.promptTextProperty().bindBidirectional(pageModel.getUserPwdFieldPromptProperty());
      loginButton.textProperty().bindBidirectional(pageModel.getLoginButtonProperty());
      progressIndicator.visibleProperty().bind(loginService.runningProperty());
   }

   /**
    * Sets the {@link LoginCallbackHandler}
    *
    * @param loginCallbackHandler
    *        the callback-Handler to set
    */
   public void setCallbackHandler(LoginCallbackHandler loginCallbackHandler) {
      this.loginCallbackHandler = requireNonNull(loginCallbackHandler);
   }

   /**
    * Sets the login-function
    *
    * @param loginFunction
    *        the actual logic to do the login
    */
   public void setLoginFunction(BiFunction<String, String, Boolean> loginFunction) {
      this.loginFunction = requireNonNull(loginFunction);
   }
}
