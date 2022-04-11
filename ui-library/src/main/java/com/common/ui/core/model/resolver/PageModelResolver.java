/**
 * 
 */
package com.common.ui.core.model.resolver;

import com.common.ui.core.control.Controller;
import com.common.ui.core.model.PageModel;

/**
 * The {@link PageModelResolver} is used by the {@link Controller} in order to
 * resolve it's appropriate {@link PageModel} with updated values
 * 
 * @author Dominic Stalder
 */
@FunctionalInterface
public interface PageModelResolver<I extends PageModel, O extends PageModel> {

   /**
    * Resolves the {@link PageModel}
    * 
    * @param dataModelIn
    * @return the new resolved {@link PageModel}
    */
   O resolvePageModel(I dataModelIn);
}
