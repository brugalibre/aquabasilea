package com.zeiterfassung.web.common;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public interface WebElementEvaluator {
   /**
    * Finds all {@link WebElement} within the given <code>searchContext</code> which matches the given by criteria and
    * predicate
    *
    * @param searchContext       the {@link WebElement} to search in
    * @param by                  the {@link By} to identify all relevant {@link WebElement}s
    * @param webElementPredicate the filter predicate for the relevant {@link WebElement}s
    * @return a {@link List} with found {@link WebElement}s
    */
   List<WebElement> findAllWebElementsByPredicateAndBy(WebElement searchContext, By by, Predicate<WebElement> webElementPredicate);

   /**
    * Tries to find an {@link WebElement} with the given id. If no such elemnet is found, an empty Optional is returned.
    * Otherwise an {@link java.util.Optional} contained the found {@link WebElement}
    *
    * @param id the {@link By} in order to locate the element
    * @return an empty {@link java.util.Optional} or an {@link java.util.Optional} containing the desired {@link WebElement}
    */
   Optional<WebElement> findElement(By id);

   /**
    * Tries to find an {@link WebElement} with the given id. If no such elemnet is found, an empty Optional is returned.
    * Otherwise an {@link java.util.Optional} contained the found {@link WebElement}
    *
    * @param webElement2FindWithing the {@link WebElement} to look at in order to find another {@link WebElement} by the given id
    * @param id                     the {@link By} in order to locate the element
    * @return an empty {@link java.util.Optional} or an {@link java.util.Optional} containing the desired {@link WebElement}
    */
   Optional<WebElement> findElementBy(WebElement webElement2FindWithing, By id);

   /**
    * Returns a {@link WebElement} for the given {@link By} or throws a {@link java.util.NoSuchElementException} if there is no such
    * element
    *
    * @param id the {@link By} in order to locate the element
    * @return the {@link WebElement}
    */
   WebElement getElement(By id);
}