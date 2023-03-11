package com.aquabasilea.web.filtercourse;

import com.aquabasilea.web.bookcourse.model.CourseBookDetails;
import com.aquabasilea.web.error.ErrorHandler;
import com.aquabasilea.web.filtercourse.filter.CourseFilter;
import com.aquabasilea.web.filtercourse.filter.CourseFilter.CourseFilterBuilder;
import com.aquabasilea.web.filtercourse.filter.CourseFilterCriterion;
import com.aquabasilea.web.filtercourse.filter.FilterType;
import com.aquabasilea.web.navigate.AquabasileaNavigatorHelper;
import com.zeiterfassung.web.common.navigate.util.WebNavigateUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.aquabasilea.web.constant.AquabasileaWebConst.*;
import static com.zeiterfassung.web.common.constant.BaseWebConst.*;

/**
 * The {@link CourseFilterHelper} accepts one or more {@link CourseFilterCriterion} (either provided directly or
 * build by a given {@link CourseBookDetails}) and uses this to apply the filters.
 * First all filters are removed, incl. the default course-location filter (Aquabasilea). Then all filters are applied
 */
public class CourseFilterHelper {
   private final Duration waitUntilCourseFilterIsReady;
   private final AquabasileaNavigatorHelper aquabasileaNavigatorHelper;
   private final static Logger LOG = LoggerFactory.getLogger(CourseFilterHelper.class);

   public CourseFilterHelper(AquabasileaNavigatorHelper aquabasileaNavigatorHelper) {
      this.aquabasileaNavigatorHelper = aquabasileaNavigatorHelper;
      this.waitUntilCourseFilterIsReady = Duration.ofMillis(40000);
   }

   /**
    * Builds a {@link CourseFilter} and applies those filters to the aquabasilea-course filter
    *
    * @param courseFilterCriteria the criteria to apply
    * @param errorHandler         the {@link ErrorHandler} to handle errors
    */
   public void applyCriteriaFilter(List<CourseFilterCriterion> courseFilterCriteria, ErrorHandler errorHandler) {
      CourseFilterBuilder courseFilterBuilder = CourseFilterBuilder.builder();
      courseFilterCriteria.forEach(courseFilterBuilder::addCourseFilterCriterion);
      applyFilterCriteria(courseFilterBuilder
              .build(), errorHandler);
   }

   /**
    * Builds a {@link CourseFilter} and applies those filters to the aquabasilea-course filter
    *
    * @param courseBookDetails the {@link CourseBookDetails} with the details for the course to book
    * @param errorHandler      the {@link ErrorHandler} to handle errors
    */
   public void applyCriteriaFilter(CourseBookDetails courseBookDetails, ErrorHandler errorHandler) {
      applyFilterCriteria(CourseFilterBuilder.builder()
              .addCourseFilterCriterion(CourseFilterCriterion.of(FilterType.COURSE_LOCATION, courseBookDetails.courseLocation()))
              .addCourseFilterCriterion(CourseFilterCriterion.of(FilterType.COURSE_NAME, courseBookDetails.courseName()))
              .addCourseFilterCriterion(CourseFilterCriterion.of(FilterType.DAY_OF_WEEK, courseBookDetails.getDayOfWeekName(Locale.GERMAN)))
              .build(), errorHandler);
   }

   private void applyFilterCriteria(CourseFilter courseFilter, ErrorHandler errorHandler) {
      LOG.info("Filtering courses for courseFilter {}...", courseFilter);
      for (CourseFilterCriterion courseFilterCriterion : courseFilter.getCourseFilterCriteria()) {
         applyFilterCriterion(errorHandler, courseFilterCriterion);
      }
      LOG.info("Done filtering courses");
   }

   private WebElement getFilterArea() {
      By xPathBy = WebNavigateUtil.createXPathBy(HTML_DIV_TYPE, WEB_ELEMENT_CRITERIA_FILTER_TABLE_ATTR_NAME, WEB_ELEMENT_CRITERIA_FILTER_TABLE_ATTR_VALUE);
      this.aquabasileaNavigatorHelper.waitForVisibilityOfElement(xPathBy, WAIT_FOR_CRITERIA_FILTER_TABLE_TO_APPEAR);
      return aquabasileaNavigatorHelper.getElement(xPathBy);
   }

   /**
    * Applies a filter criterion
    * First expand the specific filter section / sections depending on the criteria.
    * Then clicks on the criterion, matching our filter
    */
   private void applyFilterCriterion(ErrorHandler errorHandler, CourseFilterCriterion courseFilterCriterion) {
      LOG.info("Apply filter {}", courseFilterCriterion);
      FilterType filterType = courseFilterCriterion.filterType();
      waitForCourseFiltertToBeReady();

      Optional<WebElement> webElementWhichContainsSelectedCriteria = getWebElementWhichContainsSelectedCriteria(filterType);
      boolean areAllFiltersAlreadySelected = areAllFiltersAlreadySelected(courseFilterCriterion, webElementWhichContainsSelectedCriteria);
      // If there are already all criteria selected -> abort, nothing to do
      if (areAllFiltersAlreadySelected) {
         return;
      } else if (webElementWhichContainsSelectedCriteria.isPresent()) {
         unselectAllSelectedCriteria(errorHandler, webElementWhichContainsSelectedCriteria.get());
      }

      expandAllFilterCriteria(errorHandler, courseFilterCriterion, filterType);
      // Click the criteria
      for (String filterValue : courseFilterCriterion.filterValues()) {
         By filterValueXPath = filterType.createXPath(HTML_TAG_INPUT, HTML_VALUE_ATTR, filterValue);
         aquabasileaNavigatorHelper.waitUntilButtonBecameClickable(this::getFilterArea, filterValueXPath);
         aquabasileaNavigatorHelper.clickButtonOrHandleError(this::getFilterArea, filterValueXPath, errorHandler, filterValue);
      }

      // Click the filter-criterion button again -> collapse filter criteria and apply the filter.
      // Then wait again until the button is clickable, like that we are sure the loading-spinner is gone
      aquabasileaNavigatorHelper.waitUntilButtonBecameClickable(this::getFilterArea, HTML_TAG_SPAN, filterType.getUiElementText());
      aquabasileaNavigatorHelper.clickButtonOrHandleError(this::getFilterArea, filterType.getUiElementText(), HTML_TAG_SPAN, errorHandler, filterType.getUiElementText());
      aquabasileaNavigatorHelper.waitUntilButtonBecameClickable(this::getFilterArea, HTML_TAG_SPAN, filterType.getUiElementText());
   }

   private void unselectAllSelectedCriteria(ErrorHandler errorHandler, WebElement webElementWhichContainsSelectedCriteria) {
      // If not but the web-element which contains all selected criteria is present, then at least one criterion is missing -> just remove all and start over...
      List<WebElement> selectedCourseFilterCriteriaButtons = getAllSelectedCourseFilterCriteriaButtons(webElementWhichContainsSelectedCriteria);
      selectedCourseFilterCriteriaButtons.forEach(selectedCriterionButton -> aquabasileaNavigatorHelper.clickButton(selectedCriterionButton, errorHandler));
   }

   private Optional<WebElement> getWebElementWhichContainsSelectedCriteria(FilterType filterType) {
      By webElementAllSelectedCriteriaBy = WebNavigateUtil.createXPathBy(HTML_DIV_TYPE, WEB_ELEMENT_COURSE_RESULTS_CONTENT_ATTR_NAME, WEB_ELEMENT_SELECTED_COURSE_FILTERS_PREFIX_ATTR_NAME + filterType.getUiElementText());
      return aquabasileaNavigatorHelper.findWebElementBy(getFilterArea(), webElementAllSelectedCriteriaBy);
   }

   private boolean areAllFiltersAlreadySelected(CourseFilterCriterion courseFilterCriterion, Optional<WebElement> selectedFiltersWebElement) {
      if (selectedFiltersWebElement.isEmpty()) {
         return false;
      }

      List<WebElement> selectedCourseFilterCriteriaButtons = getSelectedCourseFilterCriteriaButtons4Filter(courseFilterCriterion, selectedFiltersWebElement.get());
      return selectedCourseFilterCriteriaButtons.size() == courseFilterCriterion.filterValues().size();
   }

   private List<WebElement> getSelectedCourseFilterCriteriaButtons4Filter(CourseFilterCriterion courseFilterCriterion, WebElement selectedFiltersWebElement) {
      return courseFilterCriterion.filterValues()
              .stream()
              .map(filterValue -> aquabasileaNavigatorHelper.findWebElementByTageNameAndInnerHtmlValue(selectedFiltersWebElement, HTML_BUTTON_TYPE, filterValue))
              .filter(Optional::isPresent)
              .map(Optional::get)
              .toList();
   }

   private List<WebElement> getAllSelectedCourseFilterCriteriaButtons(WebElement selectedFiltersWebElement) {
      return aquabasileaNavigatorHelper.findAllWebElementsByPredicateAndBy(selectedFiltersWebElement, By.tagName(HTML_BUTTON_TYPE), webElement -> true);
   }

   private void expandAllFilterCriteria(ErrorHandler errorHandler, CourseFilterCriterion courseFilterCriterion, FilterType filterType) {
      aquabasileaNavigatorHelper.waitUntilButtonBecameClickable(this::getFilterArea, HTML_TAG_SPAN, filterType.getUiElementText());
      aquabasileaNavigatorHelper.clickButtonOrHandleError(this::getFilterArea, filterType.getUiElementText(), HTML_TAG_SPAN, errorHandler, filterType.getUiElementText());
      expandAdditionallyFilterCriteria(errorHandler, courseFilterCriterion);
   }

   private void expandAdditionallyFilterCriteria(ErrorHandler errorHandler, CourseFilterCriterion courseFilterCriterion) {
      for (String additionallyFilterCriteriaName : courseFilterCriterion.filterType().getAdditionallyFilterCriteriaNames()) {
         aquabasileaNavigatorHelper.waitUntilButtonBecameClickable(this::getFilterArea, HTML_TAG_SPAN, additionallyFilterCriteriaName);
         aquabasileaNavigatorHelper.clickButtonOrHandleError(this::getFilterArea, additionallyFilterCriteriaName, HTML_TAG_SPAN, errorHandler, additionallyFilterCriteriaName);
      }
   }

   private void waitForCourseFiltertToBeReady() {
      this.aquabasileaNavigatorHelper.waitForElementToBeClickable(WebNavigateUtil.createXPathBy(HTML_DIV_TYPE, WEB_ELEMENT_CRITERIA_FILTER_TABLE_ATTR_NAME, WEB_ELEMENT_CRITERIA_FILTER_TABLE_ATTR_VALUE), waitUntilCourseFilterIsReady);
   }
}
