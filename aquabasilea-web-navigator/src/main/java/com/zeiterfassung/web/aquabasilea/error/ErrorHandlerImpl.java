package com.zeiterfassung.web.aquabasilea.error;

import com.zeiterfassung.web.aquabasilea.selectcourse.result.CourseClickedResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ErrorHandlerImpl implements ErrorHandler {

   private static final Logger LOG = LoggerFactory.getLogger(ErrorHandlerImpl.class);
   private List<String> errors;

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
      LOG.error(errorMsg);
      this.errors.add(errorMsg);
   }

   @Override
   public List<String> getErrors() {
      return errors;
   }
}
