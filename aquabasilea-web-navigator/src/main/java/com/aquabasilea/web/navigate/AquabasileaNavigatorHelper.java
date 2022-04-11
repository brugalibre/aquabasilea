package com.aquabasilea.web.navigate;

import com.aquabasilea.web.error.ErrorHandler;
import com.zeiterfassung.web.common.impl.navigate.BaseWebNavigatorHelper;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Supplier;

public class AquabasileaNavigatorHelper extends BaseWebNavigatorHelper {

   public static final int MAX_RETRIES = 10;
   private static final Logger LOG = LoggerFactory.getLogger(AquabasileaNavigatorHelper.class);

   public AquabasileaNavigatorHelper(WebDriver webDriver) {
      super(webDriver);
   }

   /**
    * Clicks the button or handles an error, if the button is not clickable
    * Note: This method was getting out of hand.. Right now, if a button is either not clickable, available or if there is a stale-exception
    * we retry up to MAX_RETRIES times, until we finally give up
    * @param webElementButtonOptional the optional button
    * @param buttonWebElementSupplier the {@link Supplier} which provides a new instance of an optional button
    * @param errorHandler the {@link ErrorHandler}, if anything goes wrong
    * @param elementIdentifier the identifier of the button - for logging, if anything goes wrong
    */
   public void clickButtonOrHandleError(Optional<WebElement> webElementButtonOptional, Supplier<Optional<WebElement>> buttonWebElementSupplier, ErrorHandler errorHandler, String elementIdentifier) {
      clickButtonOrHandleErrorRecursively(webElementButtonOptional, buttonWebElementSupplier, errorHandler, elementIdentifier, MAX_RETRIES);
   }

   private void clickButtonOrHandleErrorRecursively(Optional<WebElement> webElementButtonOptional, Supplier<Optional<WebElement>> buttonWebElementSupplier, ErrorHandler errorHandler, String elementIdentifier, int retriesLeft) {
      if (webElementButtonOptional.isPresent()) {
         WebElement buttonWebElement = webElementButtonOptional.get();
         LOG.info("Button '{}' available. Waiting for it to become clickable", elementIdentifier);
         try {
            waitForElementToBeClickable(buttonWebElement);
            buttonWebElement.click();
            LOG.info("Button clicked '{}'", elementIdentifier);
         } catch (ElementClickInterceptedException e) {
            LOG.error("Error while clicking button", e);
            LOG.error("Klick on button '{}' intercepted! Using script instead of direct click", elementIdentifier);
            try {
               executeClickButtonScript(buttonWebElement);
            } catch (Exception ex) {
               handleButtonClickException(buttonWebElementSupplier, errorHandler, elementIdentifier, retriesLeft, e);
            }
         } catch (Exception e) {
            handleButtonClickException(buttonWebElementSupplier, errorHandler, elementIdentifier, retriesLeft, e);
         }
      } else {
         LOG.warn("Button '{}' NOT available", elementIdentifier);
         if (retriesLeft > 0) {
            LOG.error("Do a retry. {} retries left", retriesLeft);
            clickButtonOrHandleErrorRecursively(buttonWebElementSupplier.get(), buttonWebElementSupplier, errorHandler, elementIdentifier, retriesLeft - 1);
         } else {
            LOG.error("No retries left, abort!");
            errorHandler.handleElementNotFound(elementIdentifier);
         }
      }
   }

   private void handleButtonClickException(Supplier<Optional<WebElement>> buttonWebElementSupplier, ErrorHandler errorHandler, String elementIdentifier, int retriesLeft, Exception e) {
      LOG.error("Error while clicking button", e);
      LOG.error("Klick on button '{}' failed!", elementIdentifier);
      if (retriesLeft > 0) {
         LOG.error("Do a retry. {} retries left", retriesLeft);
         clickButtonOrHandleErrorRecursively(buttonWebElementSupplier.get(), buttonWebElementSupplier, errorHandler, elementIdentifier, retriesLeft - 1);
      }
   }
}

