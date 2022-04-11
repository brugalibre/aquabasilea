package com.aquabasilea.web.error;

import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Common interface for all {@link ErrorHandler}s
 */
public interface ErrorHandler {

   /**
    * Handles an error for a non found {@link WebElement} with the given identifier
    * Note: The <code>elementIdentifier</code> can be an id or a visible text
    *
    * @param elementIdentifier the identifier of the missing element
    */
   void handleElementNotFound(String elementIdentifier);

   /**
    * handles the given error
    *
    * @param errorMsg the error to handle
    */
   void handleError(String errorMsg);

   /**
    * @return all errors of this {@link ErrorHandler}
    */
   List<String> getErrors();
}
