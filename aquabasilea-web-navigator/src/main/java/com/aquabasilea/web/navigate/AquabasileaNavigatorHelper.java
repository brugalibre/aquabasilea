package com.aquabasilea.web.navigate;

import com.aquabasilea.web.error.ErrorHandler;
import com.zeiterfassung.web.common.impl.navigate.BaseWebNavigatorHelper;
import com.zeiterfassung.web.common.impl.navigate.button.ButtonClickHelper;
import com.zeiterfassung.web.common.navigate.util.WebNavigateUtil;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Supplier;

import static com.aquabasilea.web.constant.AquabasileaWebConst.DEFAULT_TIMEOUT;
import static com.zeiterfassung.web.common.constant.BaseWebConst.HTML_BUTTON_TYPE;

public class AquabasileaNavigatorHelper extends BaseWebNavigatorHelper {

   public static final int MAX_RETRIES = 10;
   private static final Logger LOG = LoggerFactory.getLogger(AquabasileaNavigatorHelper.class);
   private final ButtonClickHelper buttonClickHelper;

   public AquabasileaNavigatorHelper(WebDriver webDriver) {
      super(webDriver);
      this.buttonClickHelper = new ButtonClickHelper(this);
   }

   /**
    * Evaluates a html-button which displays a text, contained in a child element of this very button
    * Clicks the button or handles an error, if the button is not clickable
    * Note: This method was getting out of hand.. Right now, if a button is either not clickable, available or if there is a stale-exception
    * we retry up to MAX_RETRIES times, until we finally give up
    *
    * @param searchContext       the {@link WebElement} in which the button is located
    * @param buttonInnerHtmlText the text which is displayed on the button (indirectly through an inner html-tag)
    * @param buttonInnerHtmlTag  the tag of the inner html element which displays the text
    * @param errorHandler        the {@link ErrorHandler}, if anything goes wrong
    * @param elementIdentifier   the identifier of the button - for logging, if anything goes wrong
    */
   public void clickButtonOrHandleError(WebElement searchContext, String buttonInnerHtmlText, String buttonInnerHtmlTag, ErrorHandler errorHandler, String elementIdentifier) {
      Supplier<Optional<WebElement>> buttonWebElementSupplier = () -> findParentWebElement4ChildTagNameAndInnerHtmlValue(searchContext, buttonInnerHtmlTag, buttonInnerHtmlText, HTML_BUTTON_TYPE);
      this.buttonClickHelper.clickButtonOrHandleErrorRecursively(buttonWebElementSupplier, errorHandler::handleElementNotFound, elementIdentifier, MAX_RETRIES);
   }

   /**
    * Evaluates a html-button which displays a text, contained in a child element of this very button
    * Clicks the button or handles an error, if the button is not clickable
    * Note: This method was getting out of hand.. Right now, if a button is either not clickable, available or if there is a stale-exception
    * we retry up to MAX_RETRIES times, until we finally give up
    *
    * @param searchContext     the {@link WebElement} which is used to locate the desired {@link WebElement}
    * @param by                the {@link By} to filter the button element
    * @param errorHandler      the {@link ErrorHandler}, if anything goes wrong
    * @param elementIdentifier the identifier of the button - for logging, if anything goes wrong
    */
   public void clickButtonOrHandleError(WebElement searchContext, By by, ErrorHandler errorHandler, String elementIdentifier) {
      Supplier<Optional<WebElement>> buttonWebElementSupplier = () -> findWebElementBy(searchContext, by);
      this.buttonClickHelper.clickButtonOrHandleErrorRecursively(buttonWebElementSupplier, errorHandler::handleElementNotFound, elementIdentifier, MAX_RETRIES);
   }

   /**
    * Clicks the button or handles an error, if the button (which is retrieved by the given {@link Supplier}) is not clickable
    * Note: This method was getting out of hand.. Right now, if a button is either not clickable, available or if there is a stale-exception
    * we retry up to MAX_RETRIES times, until we finally give up
    *
    * @param buttonWebElementSupplier the {@link Supplier} which provides a new instance of an optional button
    * @param errorHandler             the {@link ErrorHandler}, if anything goes wrong
    * @param elementIdentifier        the identifier of the button - for logging, if anything goes wrong
    */
   public void clickButtonOrHandleError(Supplier<Optional<WebElement>> buttonWebElementSupplier, ErrorHandler errorHandler, String elementIdentifier) {
      this.buttonClickHelper.clickButtonOrHandleErrorRecursively(buttonWebElementSupplier, errorHandler::handleElementNotFound, elementIdentifier, MAX_RETRIES);
   }

   public void clickButton(WebElement webElement, ErrorHandler errorHandler) {
      String identifier = StringUtils.isEmpty(webElement.getText()) ? "Unknown" : webElement.getText();
      this.buttonClickHelper.clickButtonOrHandleErrorRecursively(() -> Optional.of(webElement), errorHandler::handleElementNotFound, identifier, 2);
   }

   /**
    * Waits until a {@link WebElement}-button, which contains a child-{@link WebElement} with the given html-tag and inner-html-text, becomes clickable
    * We can't look up this button directly, since it has no id, unique css-class nor any other identifier. All we know is the text, which is displayed on this button
    * through an inner child-{@link WebElement}
    *
    * @param searchContext      the {@link WebElement} in which the button is located
    * @param childHtmlTag       the html-tag of the child-{@link WebElement} which is contained within the button element
    * @param childInnerHtmlText the inner-html-text of the child-{@link WebElement} which is contained within the button element
    */
   public void waitUntilButtonBecameClickable(WebElement searchContext, String childHtmlTag, String childInnerHtmlText) {
      findAndWaitUntilButtonBecameClickable(searchContext, childHtmlTag, childInnerHtmlText);
   }

   /**
    * Waits until the {@link WebElement}-button, which is defined by the given {@link By}, becomes clickable
    *
    * @param searchContext the {@link WebElement} in which the button is located
    * @param by            the {@link By} to locate the {@link WebElement}
    */
   public void waitUntilButtonBecameClickable(WebElement searchContext, By by) {
      Optional<WebElement> buttonWebElement = findWebElementBy(searchContext, by);
      String childHtmlTag = buttonWebElement.map(WebElement::getText).orElse("unknown inner html");
      String childInnerHtmlText = buttonWebElement.map(WebElement::getTagName).orElse("unknown tag-name");
      findAndWaitUntilButtonBecameClickableInternal(searchContext, buttonWebElement, childHtmlTag, childInnerHtmlText, 3);
   }

   private void findAndWaitUntilButtonBecameClickable(WebElement searchContext, String childHtmlTag, String childInnerHtmlText) {
      Optional<WebElement> buttonWebElement = findParentWebElement4ChildTagNameAndInnerHtmlValue(searchContext, childHtmlTag, childInnerHtmlText, HTML_BUTTON_TYPE);
      findAndWaitUntilButtonBecameClickableInternal(searchContext, buttonWebElement, childHtmlTag, childInnerHtmlText, 3);
   }

   private void findAndWaitUntilButtonBecameClickableInternal(WebElement searchContext, Optional<WebElement> buttonWebElement, String childHtmlTag, String childInnerHtmlText, int retries) {
      if (buttonWebElement.isPresent()) {
         waitForElementToBeClickable(buttonWebElement.get());
         WebNavigateUtil.waitForMilliseconds(DEFAULT_TIMEOUT);
         LOG.info("Button found which contains an inner child from type {} and with text {}. Retries left {}", childHtmlTag, childInnerHtmlText, retries);
      } else if (retries > 0) {
         WebNavigateUtil.waitForMilliseconds(DEFAULT_TIMEOUT);
         retries--;
         LOG.warn("No button found which contains an inner child from type {} and with text {}! Let's retry - retries left {}", childHtmlTag, childInnerHtmlText, retries);
         findAndWaitUntilButtonBecameClickableInternal(searchContext, buttonWebElement, childHtmlTag, childInnerHtmlText, retries);
      } else {
         LOG.error("No button found which contains an inner child from type {} and with text {}!", childHtmlTag, childInnerHtmlText);
      }
   }
}

