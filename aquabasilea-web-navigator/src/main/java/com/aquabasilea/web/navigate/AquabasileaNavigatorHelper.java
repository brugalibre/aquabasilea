package com.aquabasilea.web.navigate;

import com.aquabasilea.web.error.ErrorHandler;
import com.zeiterfassung.web.common.impl.navigate.BaseWebNavigatorHelper;
import com.zeiterfassung.web.common.impl.navigate.button.ButtonClickHelper;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Supplier;

public class AquabasileaNavigatorHelper extends BaseWebNavigatorHelper {

   private static final Logger LOG = LoggerFactory.getLogger(AquabasileaNavigatorHelper.class);
   public static final int MAX_RETRIES = 10;
   private final ButtonClickHelper buttonClickHelper;

   public AquabasileaNavigatorHelper(WebDriver webDriver) {
      super(webDriver);
      this.buttonClickHelper = new ButtonClickHelper(this);
   }

   /**
    * Clicks the button or handles an error, if the button is not clickable
    * Note: This method was getting out of hand.. Right now, if a button is either not clickable, available or if there is a stale-exception
    * we retry up to MAX_RETRIES times, until we finally give up
    *
    * @param webElementButtonOptional the optional button
    * @param buttonWebElementSupplier the {@link Supplier} which provides a new instance of an optional button
    * @param errorHandler             the {@link ErrorHandler}, if anything goes wrong
    * @param elementIdentifier        the identifier of the button - for logging, if anything goes wrong
    */
   public void clickButtonOrHandleError(Optional<WebElement> webElementButtonOptional, Supplier<Optional<WebElement>> buttonWebElementSupplier, ErrorHandler errorHandler, String elementIdentifier) {
      this.buttonClickHelper.clickButtonOrHandleErrorRecursively(webElementButtonOptional, buttonWebElementSupplier, errorHandler::handleError, elementIdentifier, MAX_RETRIES);
   }

   public void clickButton(WebElement webElement, ErrorHandler errorHandler) {
      String identifier = StringUtils.isEmpty(webElement.getText()) ? "Unknown" : webElement.getText();
      this.buttonClickHelper.clickButtonOrHandleErrorRecursively(Optional.of(webElement), () -> Optional.of(webElement), errorHandler::handleError, identifier, 2);
   }
}

