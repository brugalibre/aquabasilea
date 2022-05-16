package com.aquabasilea.web.error;

import java.util.ArrayList;
import java.util.List;

public class ErrorHandlerImpl implements ErrorHandler {

   private final List<String> errors;

   public ErrorHandlerImpl() {
      this.errors = new ArrayList<>();
   }

   @Override
   public void handleElementNotFound(String elementIdentifier) {
      String errorMsg = String.format("Element not found with identifier '%s'!", elementIdentifier);
      handleError(errorMsg);
   }

   @Override
   public void handleError(String errorMsg) {
      this.errors.add(errorMsg);
   }

   @Override
   public List<String> getErrors() {
      return errors;
   }
}
