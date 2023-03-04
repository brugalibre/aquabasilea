package com.aquabasilea.web.navigate;

import com.aquabasilea.web.constant.AquabasileaWebConst;
import com.aquabasilea.web.error.ErrorHandler;
import com.zeiterfassung.web.common.impl.navigate.BaseWebNavigatorHelper;
import com.zeiterfassung.web.common.impl.navigate.button.ButtonClickHelper;
import com.zeiterfassung.web.common.navigate.util.WebNavigateUtil;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;

import static com.aquabasilea.web.constant.AquabasileaWebConst.DEFAULT_TIMEOUT;
import static com.zeiterfassung.web.common.constant.BaseWebConst.HTML_BUTTON_TYPE;

public class AquabasileaNavigatorHelper extends BaseWebNavigatorHelper {

   private static final int CLICK_BUTTON_RETRIES_IF_ERROR = 10;
   private static final int RETRY_COUNT_WHEN_BUTTON_NOT_AVAILABLE_WHEN_BECOMING_CLICKABLE = 3;
   private static final Logger LOG = LoggerFactory.getLogger(AquabasileaNavigatorHelper.class);
   private final Duration durationUntilButtonBecomesClickable;
   private final Duration durationUntilLoadingAnimationDisappear;
   private final ButtonClickHelper buttonClickHelper;

   public AquabasileaNavigatorHelper(WebDriver webDriver, Duration durationUntilLoadingAnimationDisappear) {
      super(webDriver);
      this.buttonClickHelper = new ButtonClickHelper(this);
      this.durationUntilLoadingAnimationDisappear = durationUntilLoadingAnimationDisappear;
      this.durationUntilButtonBecomesClickable = Duration.ofMillis(20000);
   }

   /**
    * Evaluates a html-button which displays a text, contained in a child element of this very button
    * Clicks the button or handles an error, if the button is not clickable
    * Note: This method was getting out of hand.. Right now, if a button is either not clickable, available or if there is a stale-exception
    * we retry up to CLICK_BUTTON_RETRIES_IF_ERROR times, until we finally give up
    *
    * @param searchContextSuppIn the {@link Supplier} which provides a {@link WebElement} in which the button is located
    * @param buttonInnerHtmlText the text which is displayed on the button (indirectly through an inner html-tag)
    * @param buttonInnerHtmlTag  the tag of the inner html element which displays the text
    * @param errorHandler        the {@link ErrorHandler}, if anything goes wrong
    * @param elementIdentifier   the identifier of the button - for logging, if anything goes wrong
    */
   public void clickButtonOrHandleError(Supplier<WebElement> searchContextSuppIn, String buttonInnerHtmlText, String buttonInnerHtmlTag, ErrorHandler errorHandler, String elementIdentifier) {
      Supplier<Optional<WebElement>> buttonWebElementSupplier = () -> findParentWebElement4ChildTagNameAndInnerHtmlValue(searchContextSuppIn.get(), buttonInnerHtmlTag, buttonInnerHtmlText, HTML_BUTTON_TYPE);
      waitForWaitingAnimationToDisappear();
      this.buttonClickHelper.clickButtonOrHandleErrorRecursively(buttonWebElementSupplier, errorHandler::handleElementNotFound, elementIdentifier, CLICK_BUTTON_RETRIES_IF_ERROR);
   }

   /**
    * Evaluates a html-button which displays a text, contained in a child element of this very button
    * Clicks the button or handles an error, if the button is not clickable
    * Note: This method was getting out of hand.. Right now, if a button is either not clickable, available or if there is a stale-exception
    * we retry up to CLICK_BUTTON_RETRIES_IF_ERROR times, until we finally give up
    *
    * @param searchContextSupp the {@link Supplier} which provides a {@link WebElement} in which the button is located
    * @param by                the {@link By} to filter the button element
    * @param errorHandler      the {@link ErrorHandler}, if anything goes wrong
    * @param elementIdentifier the identifier of the button - for logging, if anything goes wrong
    */
   public void clickButtonOrHandleError(Supplier<WebElement> searchContextSupp, By by, ErrorHandler errorHandler, String elementIdentifier) {
      Supplier<Optional<WebElement>> buttonWebElementSupplier = () -> findWebElementBy(searchContextSupp.get(), by);
      this.buttonClickHelper.clickButtonOrHandleErrorRecursively(buttonWebElementSupplier, errorHandler::handleElementNotFound, elementIdentifier, CLICK_BUTTON_RETRIES_IF_ERROR);
   }

   /**
    * Clicks the button or handles an error, if the button (which is retrieved by the given {@link Supplier}) is not clickable
    * Note: This method was getting out of hand.. Right now, if a button is either not clickable, available or if there is a stale-exception
    * we retry up to CLICK_BUTTON_RETRIES_IF_ERROR times, until we finally give up
    *
    * @param buttonWebElementSupplier the {@link Supplier} which provides a new instance of an optional button
    * @param errorHandler             the {@link ErrorHandler}, if anything goes wrong
    * @param elementIdentifier        the identifier of the button - for logging, if anything goes wrong
    */
   public void clickButtonOrHandleError(Supplier<Optional<WebElement>> buttonWebElementSupplier, ErrorHandler errorHandler, String elementIdentifier) {
      this.buttonClickHelper.clickButtonOrHandleErrorRecursively(buttonWebElementSupplier, errorHandler::handleElementNotFound, elementIdentifier, CLICK_BUTTON_RETRIES_IF_ERROR);
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
    * @param searchContextSuppIn the {@link Supplier} which provides a {@link WebElement} in which the button is located
    * @param childHtmlTag        the html-tag of the child-{@link WebElement} which is contained within the button element
    * @param childInnerHtmlText  the inner-html-text of the child-{@link WebElement} which is contained within the button element
    */
   public void waitUntilButtonBecameClickable(Supplier<WebElement> searchContextSuppIn, String childHtmlTag, String childInnerHtmlText) {
      findAndWaitUntilButtonBecameClickable(searchContextSuppIn, childHtmlTag, childInnerHtmlText);
   }

   /**
    * Waits until the {@link WebElement}-button, which is defined by the given {@link By}, becomes clickable
    *
    * @param searchContextSuppIn the {@link Supplier} which provides a {@link WebElement} in which the button is located
    * @param by                  the {@link By} to locate the {@link WebElement}
    */
   public void waitUntilButtonBecameClickable(Supplier<WebElement> searchContextSuppIn, By by) {
      Supplier<WebElement> searchContextSupp = searchContextSuppIn == null ? () -> null : searchContextSuppIn;
      Supplier<Optional<WebElement>> buttonWebElementSupplier = () -> findWebElementBy(searchContextSupp.get(), by);
      String childHtmlTag = buttonWebElementSupplier.get().map(WebElement::getText).orElse("unknown inner html");
      String childInnerHtmlText = buttonWebElementSupplier.get().map(WebElement::getTagName).orElse("unknown tag-name");
      findAndWaitUntilButtonBecameClickableInternal(buttonWebElementSupplier, childHtmlTag, childInnerHtmlText, RETRY_COUNT_WHEN_BUTTON_NOT_AVAILABLE_WHEN_BECOMING_CLICKABLE);
   }

   private void findAndWaitUntilButtonBecameClickable(Supplier<WebElement> searchContextSuppIn, String childHtmlTag, String childInnerHtmlText) {
      Supplier<WebElement> searchContextSupp = searchContextSuppIn == null ? () -> null : searchContextSuppIn;
      Supplier<Optional<WebElement>> buttonWebElementSupplier = () -> findParentWebElement4ChildTagNameAndInnerHtmlValue(searchContextSupp.get(), childHtmlTag, childInnerHtmlText, HTML_BUTTON_TYPE);
      findAndWaitUntilButtonBecameClickableInternal(buttonWebElementSupplier, childHtmlTag, childInnerHtmlText, RETRY_COUNT_WHEN_BUTTON_NOT_AVAILABLE_WHEN_BECOMING_CLICKABLE);
   }

   private void findAndWaitUntilButtonBecameClickableInternal(Supplier<Optional<WebElement>> buttonWebElementSupplier, String childHtmlTag, String childInnerHtmlText, int retries) {
      // Wait until the loading animation disappears (maybe it's not shown at all, but maybe it is. You never know)
      waitForWaitingAnimationToDisappear();
      Optional<WebElement> buttonWebElement = buttonWebElementSupplier.get();
      if (buttonWebElement.isPresent()) {
         waitForElementToBeClickable(buttonWebElement.get(), durationUntilButtonBecomesClickable);
         WebNavigateUtil.waitForMilliseconds(DEFAULT_TIMEOUT);
         LOG.info("Button found with text '{}' which contains an inner child from type {}", childInnerHtmlText, childHtmlTag);
      } else if (retries > 0) {
         WebNavigateUtil.waitForMilliseconds(DEFAULT_TIMEOUT);
         retries--;
         LOG.warn("No button found which contains an inner child from type {} and with text {}! Let's retry - retries left {}", childHtmlTag, childInnerHtmlText, retries);
         findAndWaitUntilButtonBecameClickableInternal(buttonWebElementSupplier, childHtmlTag, childInnerHtmlText, retries);
      } else {
         LOG.error("No button found which contains an inner child from type {} and with text {}!", childHtmlTag, childInnerHtmlText);
      }
   }

   private void waitForWaitingAnimationToDisappear() {
      waitForInvisibilityOfElementBy(By.cssSelector(AquabasileaWebConst.LOADING_ANIMATION_CLASS_NAME), durationUntilLoadingAnimationDisappear.toMillis());
   }
}

