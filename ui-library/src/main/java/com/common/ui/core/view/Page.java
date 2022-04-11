package com.common.ui.core.view;

import com.common.ui.core.control.Controller;
import com.common.ui.core.model.PageModel;
import javafx.stage.Stage;


/**
 * A {@link Page} content one ore more views and displays a main content
 * of the application. Before a {@link Page} is shown, it is <br>
 * initialized
 *
 * @author Dominic Stalder
 */
public interface Page<I extends PageModel, O extends PageModel> {

   /**
    * Returns the controller of this Page
    *
    * @return the controller of this Page
    */
   Controller<I, O> getController();

   /**
    * Returns <code>true</code> if this Page is dirty and needs to be refreshed or
    * <code>false</code> if not
    *
    * @return <code>true</code> if this Page is dirty and needs to be refreshed or
    * <code>false</code> if not
    */
   default boolean isDirty() {
      return true;
   }

   /**
    * @return the {@link Stage} of this Page
    */
   Stage getStage();

   /**
    * Shows this {@link Page}
    */
   void show();

   /**
    * Hides this {@link Page}
    */
   void hide();

   /**
    * @return <code>true</code> if the caller is blocked until this page is hidden or <code>false</code> if not
    */
   boolean isBlocking();
}