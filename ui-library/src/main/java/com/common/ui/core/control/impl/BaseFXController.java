/**
 * 
 */
package com.common.ui.core.control.impl;

import com.common.ui.core.control.Controller;
import com.common.ui.core.model.PageModel;
import com.common.ui.core.view.Page;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

/**
 * The {@link BaseFXController} provides the most basic features any
 * {@link Controller} should provide such as refreshing the current visible page
 * 
 * @author Dominic Stalder
 */
public abstract class BaseFXController<I extends PageModel, O extends PageModel>
      extends BaseController<I, O> implements Initializable {

   @FXML
   protected Pane rootPane;

   protected BaseFXController() {
      super();
   }

   @Override
   public void initialize(URL arg0, ResourceBundle arg1) {
      // Nothing to do by default
   }

   @Override
   public void show(I dataModelIn) {
      initDataModel(dataModelIn);
      Optional<Stage> optionalStage = getStage(page);
      optionalStage.ifPresent(showStage());
   }

   private Consumer<? super Stage> showStage() {
      return page.isBlocking() ? Stage::showAndWait : Stage::show;
   }

   @Override
   public void hide() {
      Optional<Stage> optionalStage = getStage(page);
      optionalStage.ifPresent(Stage::hide);
   }

   private Optional<Stage> getStage(Page<?, ?> page) {
      return Optional.of(page.getStage());
   }

   protected Stage getStage() {
      return getStage(page)
            .orElseThrow(IllegalStateException::new);
   }

   /**
    * @return the root Pane of this {@link BaseFXController}
    */
   public final Pane getRootPane() {
      return rootPane;
   }
}
