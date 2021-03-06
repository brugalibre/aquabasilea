package com.common.ui.core.view.impl;

import com.common.ui.core.control.Controller;
import com.common.ui.core.model.PageModel;
import com.common.ui.core.view.Page;

import java.awt.*;

/**
 * An {@link AbstractPage} is a kind of huge {@link Panel} which may content
 * different sub-Panels. Those sub Panels can be added <br>
 * to a {@link Page} in order to display them.
 * 
 * @author Dominic Stalder
 * @param <O>
 *        - the outgoing data-model
 * @param <I>
 *        - the incoming data-model
 *
 */
public abstract class AbstractPage<I extends PageModel, O extends PageModel> implements Page<I, O> {
   private Controller<I, O> controller;
   protected boolean isBlocking;

   /**
    * 
    * @param isBlocking
    *        <code>true</code> if the caller is blocked until this page is hidden or <code>false</code> if not
    */
   protected AbstractPage(boolean isBlocking) {
      super();
      this.isBlocking = isBlocking;
      initialize();
   }

   protected abstract void initialize();

   @Override
   public boolean isBlocking() {
      return isBlocking;
   }

   /**
    * @return the controller
    */
   @Override
   public Controller<I, O> getController() {
      return controller;
   }

   /**
    * @param controller
    *        the controller to set
    */
   protected void setController(Controller<I, O> controller) {
      this.controller = controller;
   }
}
