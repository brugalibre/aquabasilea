/**
 * 
 */
package com.common.ui.core.control;

import com.common.ui.core.model.PageModel;
import com.common.ui.core.view.Page;

/**
 * The {@link Controller} as part of the MVC keeps controll about the ui
 * 
 * @author Dominic
 *
 * @param <I>
 *        the incoming data model
 * @param <O>
 *        the outgoing data model
 */
public interface Controller<I extends PageModel, O extends PageModel> {
   /**
    * Initializes this Controller with it's main page to show
    * 
    * @param page
    *        - the main page
    */
   void initialize(Page<I, O> page);

   /**
    * This leads this {@link Controller} to show it's content, given the initial datamodel
    * 
    * @param dataModelIn
    *        the incoming {@link PageModel}
    */
   void show(I dataModelIn);

   /**
    * Leads this {@link Controller} to hides it's content
    */
   void hide();

   /**
    * Forces this Controller to refresh it's content and, if desired, the content
    * of it's subpages
    */
   void refresh();
}
