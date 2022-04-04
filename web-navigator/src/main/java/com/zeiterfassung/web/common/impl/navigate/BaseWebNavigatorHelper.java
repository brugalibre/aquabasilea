package com.zeiterfassung.web.common.impl.navigate;

import com.zeiterfassung.web.common.WebElementEvaluator;
import com.zeiterfassung.web.common.impl.WebElementEvaluatorImpl;
import com.zeiterfassung.web.common.navigate.util.WebNavigateUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static com.zeiterfassung.web.common.constant.BaseWebConst.CLICK_BUTTON_SCRIPT;
import static java.util.Objects.requireNonNull;

public class BaseWebNavigatorHelper {

   protected WebDriver webDriver;
   protected WebElementEvaluator webElementEvaluator;

   public BaseWebNavigatorHelper(WebDriver webDriver) {
      this.webDriver = requireNonNull(webDriver);
      this.webElementEvaluator = new WebElementEvaluatorImpl(webDriver);
   }

   /**
    * Returns the {@link WebElement} matching to the given {@link By}
    *
    * @param by the by to filter for
    * @return the {@link WebElement} matching to the given {@link By}
    */
   public WebElement getElement(By by) {
      return webElementEvaluator.getElement(by);
   }

   /**
    * Tries to find an {@link WebElement} with the given id. If no such element is found, an empty Optional is returned.
    * Otherwise an {@link Optional} contained the found {@link WebElement}
    *
    * @param id the {@link By} in order to locate the element
    * @return an empty {@link Optional} or an {@link Optional} containing the desired {@link WebElement}
    */
   public Optional<WebElement> findWebElementById(String id) {
      return webElementEvaluator.findElement(By.id(id));
   }

   /**
    * Finds a {@link WebElement} by a {@link By#name(String)} which matches the given <code>tagName</code>.
    * Since this is not necessarily unique the {@link WebElement} must contain an attribute with the given name and value
    * <p>
    * If there is more than one match, the first one is returned. If none if found, an empty Optional is returned
    *
    * @param searchContext     the search context to look in
    * @param tagName           the value of the 'tag' attribute
    * @param attrName          the name of an additional attribute to query
    * @param expectedAttrValue the value of the additional attribute
    * @return the first found web-element - if there is more than one
    */
   public Optional<WebElement> findWebElementByNameTagNameAndValue(WebElement searchContext, String tagName, String attrName, String expectedAttrValue) {
      return this.findWebElementBy(searchContext, WebNavigateUtil.createXPathBy(tagName, attrName, expectedAttrValue));
   }

   /**
    * Gets a {@link WebElement} by a {@link By#name(String)} which matches the given <code>tagName</code>.
    * Since this is not necessarily unique the {@link WebElement} must contain an attribute with the given name and value
    * <p>
    * If there is more than one match, the first one is returned.
    *
    * @param searchContext     the search context to look in
    * @param tagName           the value of the 'tag' attribute
    * @param attrName          the name of an additional attribute to query
    * @param expectedAttrValue the value of the additional attribute
    * @return the first found web-element - if there is more than one
    */
   public WebElement getWebElementByNameTagNameAndValue(WebElement searchContext, String tagName, String attrName, String expectedAttrValue) {
      return findWebElementBy(searchContext, WebNavigateUtil.createXPathBy(tagName, attrName, expectedAttrValue)).get();
   }

   /**
    * Finds a {@link WebElement} by a {@link By#name(String)} which matches the given <code>tag-name</code> and the
    * displayed html value. If there is more than one match, the first one is returned
    *
    * @param searchContext          the search context to look in
    * @param tagName                the tagName of the {@link WebElement}, e.g. 'Button' or 'span'
    * @param expectedInnerHtmlValue the value of the inner html of the {@link WebElement
    * @return the first found web-element - if there is more than one
    */
   public WebElement getWebElementByTageNameAndInnerHtmlValue(WebElement searchContext, String tagName, String expectedInnerHtmlValue) {
      return findWebElementByPredicateAndBy(searchContext, By.tagName(tagName), webElement4TagName -> expectedInnerHtmlValue.equals(webElement4TagName.getText())).get();
   }

   /**
    * Finds a {@link WebElement} by a {@link By#tagName(String)} which matches the given <code>tag-name</code> and the
    * displayed html value. If there is more than one match, the first one is returned.
    * An empty Optional is returned, if no element is found
    *
    * @param searchContext          the search context to look in
    * @param tagName                the tagName of the {@link WebElement}, e.g. 'Button' or 'span'
    * @param expectedInnerHtmlValue the value of the inner html of the {@link WebElement
    * @return the first found web-element - if there is more than one
    */
   public Optional<WebElement> findWebElementByTageNameAndInnerHtmlValue(WebElement searchContext, String tagName, String expectedInnerHtmlValue) {
      return findWebElementByPredicateAndBy(searchContext, By.tagName(tagName), webElement4TagName -> expectedInnerHtmlValue.equals(webElement4TagName.getText()));
   }

   /**
    * Finds a parent {@link WebElement} for a child, defined by the given <code>tagName</code> and <code>expectedInnerHtmlValue</code>
    * The parent can be matched with the given <code>parentsWebElementType</code>.
    * <p>
    * The tree with {@link WebElement} is traversed, using a <code>By.xpath("./..")</code> starting by the given child
    * <p>
    * This method is simply a convinient variant of calling
    *
    * @param searchContext          the search context to look in
    * @param tagName                the tagName of the {@link WebElement}, e.g. 'Button' or 'span'
    * @param expectedInnerHtmlValue the value of the inner html of the {@link WebElement
    * @param parentsWebElementType  the type of the parent
    * @return the first {@link WebElement} which is a parent of the given child and matches the given predicate
    * @see findWebElementByTageNameAndInnerHtmlValue
    */
   public Optional<WebElement> findParentWebElement4ChildTagNameAndInnerHtmlValue(WebElement searchContext, String tagName, String expectedInnerHtmlValue, String parentsWebElementType) {
      WebElement childWebElement = getWebElementByTageNameAndInnerHtmlValue(searchContext, tagName, expectedInnerHtmlValue);
      return findParentWebElement4ChildAndType(childWebElement, parentsWebElementType);
   }

   /**
    * Finds a parent {@link WebElement} for the given <code>childWebElement</code> and the type of {@link WebElement} of
    * its parent
    * The parent can be matched with the given type. The tree with {@link WebElement} is traversed, using
    * a <code>By.xpath("./..")</code> starting by the given child
    *
    * @param childWebElement       the child {@link WebElement}
    * @param parentsWebElementType the type of the parent
    * @return the first {@link WebElement} which is a parent of the given child and matches the given predicate
    */
   public Optional<WebElement> findParentWebElement4ChildAndType(WebElement childWebElement, String parentsWebElementType) {
      Predicate<WebElement> webElementPredicate = webElement2Check -> webElement2Check.getTagName().equals(parentsWebElementType);
      return findParentWebElement4ChildAndType(childWebElement, webElementPredicate);
   }

   /**
    * Finds all {@link WebElement} within the given <code>searchContext</code> which matches the given by criteria and
    * predicate
    *
    * @param searchContext       the {@link WebElement} to search in
    * @param by                  the {@link By} to identify all relevant {@link WebElement}s
    * @param webElementPredicate the filter predicate for the relevant {@link WebElement}s
    * @return a {@link List} with found {@link WebElement}s
    */
   public List<WebElement> findAllWebElementsByPredicateAndBy(WebElement searchContext, By by, Predicate<WebElement> webElementPredicate) {
      return webElementEvaluator.findAllWebElementsByPredicateAndBy(searchContext, by, webElementPredicate);
   }

   public Optional<WebElement> findWebElementByPredicateAndBy(WebElement searchContext, By by, Predicate<WebElement> webElementPredicate) {
      List<WebElement> webElementsByNameAndValue = webElementEvaluator.findAllWebElementsByPredicateAndBy(searchContext, by, webElementPredicate);
      return webElementsByNameAndValue.isEmpty() ? Optional.empty() : Optional.of(webElementsByNameAndValue.get(0));
   }

   public Optional<WebElement> findWebElementBy(WebElement searchContext, By by) {
      List<WebElement> webElementsByNameAndValue = webElementEvaluator.findAllWebElementsByPredicateAndBy(searchContext, by, webElement -> true);
      return webElementsByNameAndValue.isEmpty() ? Optional.empty() : Optional.of(webElementsByNameAndValue.get(0));
   }

   /**
    * Finds a parent {@link WebElement} for the given <code>childWebElement</code>.
    * The parent can be matched with the given {@link Predicate}. The tree with {@link WebElement} is traversed, using
    * a <code>By.xpath("./..")</code> starting by the given child
    *
    * @param childWebElement     the child {@link WebElement}
    * @param findParentPredicate the Predicate to identify the parent within the tree of {@link WebElement}s
    * @return the first {@link WebElement} which is a parent of the given child and matches the given predicate.
    * If none is found, then an empty Optionals is returned
    */
   public Optional<WebElement> findParentWebElement4ChildAndType(WebElement childWebElement, Predicate<WebElement> findParentPredicate) {
      requireNonNull(findParentPredicate, "findParentPredicate must not be null");
      requireNonNull(childWebElement, "childWebElement must not be null");
      Optional<WebElement> nextWebElementOpt = webElementEvaluator.findElementBy(childWebElement, By.xpath("./.."));
      while (nextWebElementOpt.isPresent()
              && !findParentPredicate.test(nextWebElementOpt.get())) {
         nextWebElementOpt = webElementEvaluator.findElementBy(nextWebElementOpt.get(), By.xpath("./.."));
      }
      return nextWebElementOpt;
   }

   public void waitForVisibilityOfElement(By by, long millis) {
      WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofMillis(millis));
      wait.until(ExpectedConditions.visibilityOfElementLocated(by));
   }

   public void waitForElementToBeClickable(WebElement webElement) {
      WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofMillis(6000));
      wait.until(ExpectedConditions.elementToBeClickable(webElement));
   }

   public void waitForInvisibilityOfElement(WebElement webElement) {
      WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofMillis(4000));
      wait.until(ExpectedConditions.invisibilityOf(webElement));
   }

   public void waitForInvisibilityOfElementBy(By by) {
      WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofMillis(4000));
      wait.until(ExpectedConditions.invisibilityOfElementLocated(by));
   }

   public void executeClickButtonScript(WebElement webElement) {
      executeScript(CLICK_BUTTON_SCRIPT, webElement);
   }

   protected void executeScript(String script, WebElement webElement) {
      JavascriptExecutor executor = (JavascriptExecutor) webDriver;
      executor.executeScript(script, webElement);
   }
}