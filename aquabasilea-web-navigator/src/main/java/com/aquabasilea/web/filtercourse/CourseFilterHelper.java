package com.aquabasilea.web.filtercourse;

import com.aquabasilea.web.error.ErrorHandler;
import com.aquabasilea.web.filtercourse.filter.CourseFilter;
import com.aquabasilea.web.filtercourse.filter.CourseFilter.CourseFilterBuilder;
import com.aquabasilea.web.filtercourse.filter.CourseFilterCriterion;
import com.aquabasilea.web.filtercourse.filter.FilterType;
import com.aquabasilea.web.navigate.AquabasileaNavigatorHelper;
import com.zeiterfassung.web.common.navigate.util.WebNavigateUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import static com.aquabasilea.web.constant.AquabasileaWebConst.*;
import static com.zeiterfassung.web.common.constant.BaseWebConst.*;

public class CourseFilterHelper {
   private final AquabasileaNavigatorHelper aquabasileaNavigatorHelper;

   public CourseFilterHelper(AquabasileaNavigatorHelper aquabasileaNavigatorHelper) {
      this.aquabasileaNavigatorHelper = aquabasileaNavigatorHelper;
   }

   /**
    * Builds a {@link CourseFilter} and applies those filters to the aquabasilea-course filter
    *
    * @param courseFilterCriteria the criteria to apply
    * @param errorHandler the {@link ErrorHandler} to handle errors
    */
   public void applyCriteriaFilter(List<CourseFilterCriterion> courseFilterCriteria, ErrorHandler errorHandler) {
      CourseFilterBuilder courseFilterBuilder = CourseFilterBuilder.builder();
      courseFilterCriteria.forEach(courseFilterBuilder::addCourseFilterCriterion);
      removeDefaultCourseLocationFilter(errorHandler);
      applyFilterCriteria(courseFilterBuilder
              .build(), errorHandler);
   }

   /**
    * Builds a {@link CourseFilter} and applies those filters to the aquabasilea-course filter
    *
    * @param courseName   the name of the course
    * @param dayOfWeek    the day, on which the course takes place
    * @param errorHandler the {@link ErrorHandler} to handle errors
    */
   public void applyCriteriaFilter(String courseName, DayOfWeek dayOfWeek, ErrorHandler errorHandler) {
      applyFilterCriteria(CourseFilterBuilder.builder()
              .addCourseFilterCriterion(CourseFilterCriterion.of(FilterType.COURSE_NAME, courseName))
              .addCourseFilterCriterion(CourseFilterCriterion.of(FilterType.DAY_OF_WEEK, dayOfWeek.getDisplayName(TextStyle.FULL, Locale.GERMAN)))
              .build(), errorHandler);
   }

   private void applyFilterCriteria(CourseFilter courseFilter, ErrorHandler errorHandler) {
      for (CourseFilterCriterion courseFilterCriterion : courseFilter.getCourseFilterCriteria()) {
         WebElement filterArea = aquabasileaNavigatorHelper.getWebElementByNameTagNameAndValue(null, HTML_DIV_TYPE, WEB_ELEMENT_CRITERIA_FILTER_TABLE_ATTR_NAME, WEB_ELEMENT_CRITERIA_FILTER_TABLE_ATTR_VALUE);
         applyFilterCriterion(errorHandler, filterArea, courseFilterCriterion);
      }
      this.aquabasileaNavigatorHelper.waitForVisibilityOfElement(WebNavigateUtil.createXPathBy(HTML_DIV_TYPE, WEB_ELEMENT_COURSE_RESULTS_CONTENT_ATTR_NAME, WEB_ELEMENT_COURSE_RESULTS_CONTENT_ATTR_VALUE), 20000);
   }

   /**
    * Applies a filter criterion
    * First expand the specific filter section / sections depending on the criteria.
    * Then clicks on the criterion, matching our filter
    */
   private void applyFilterCriterion(ErrorHandler errorHandler, WebElement filterArea, CourseFilterCriterion courseFilterCriterion) {
      FilterType filterType = courseFilterCriterion.getFilterType();
      expandAllFilterCriteria(filterArea, errorHandler, courseFilterCriterion, filterType);

      // Click the criteria
      for (String filterValue : courseFilterCriterion.getFilterValues()) {
         By filterValueXPath = filterType.createXPath(HTML_TAG_INPUT, HTML_VALUE_ATTR, filterValue);
         aquabasileaNavigatorHelper.waitUntilButtonBecameClickable(filterArea, filterValueXPath);
         aquabasileaNavigatorHelper.clickButtonOrHandleError(filterArea, filterValueXPath, errorHandler, filterValue);
      }

      // Click the filter-criterion button again -> collapse filter criteria and apply the filter
      aquabasileaNavigatorHelper.clickButtonOrHandleError(filterArea, filterType.getUiElementText(), HTML_TAG_SPAN, errorHandler, filterType.getUiElementText());
      aquabasileaNavigatorHelper.waitUntilButtonBecameClickable(filterArea, HTML_TAG_SPAN, filterType.getUiElementText());
   }

   private void expandAllFilterCriteria(WebElement filterArea, ErrorHandler errorHandler, CourseFilterCriterion courseFilterCriterion, FilterType filterType) {
      aquabasileaNavigatorHelper.waitUntilButtonBecameClickable(filterArea, HTML_TAG_SPAN, filterType.getUiElementText());
      aquabasileaNavigatorHelper.clickButtonOrHandleError(filterArea, filterType.getUiElementText(), HTML_TAG_SPAN, errorHandler, filterType.getUiElementText());
      expandAdditionallyFilterCriteria(filterArea, errorHandler, courseFilterCriterion);
   }

   private void expandAdditionallyFilterCriteria( WebElement filterArea, ErrorHandler errorHandler, CourseFilterCriterion courseFilterCriterion) {
      for (String additionallyFilterCriteriaName : courseFilterCriterion.getFilterType().getAdditionallyFilterCriteriaNames()) {
         aquabasileaNavigatorHelper.waitUntilButtonBecameClickable(filterArea, HTML_TAG_SPAN, additionallyFilterCriteriaName);
         aquabasileaNavigatorHelper.clickButtonOrHandleError(filterArea, additionallyFilterCriteriaName, HTML_TAG_SPAN, errorHandler, additionallyFilterCriteriaName);
      }
   }

   private void removeDefaultCourseLocationFilter(ErrorHandler errorHandler) {
      WebElement filterArea = aquabasileaNavigatorHelper.getWebElementByNameTagNameAndValue(null, HTML_DIV_TYPE, WEB_ELEMENT_CRITERIA_FILTER_TABLE_ATTR_NAME, WEB_ELEMENT_CRITERIA_FILTER_TABLE_ATTR_VALUE);
      aquabasileaNavigatorHelper.clickButtonOrHandleError(filterArea, WEB_BUTTON_MIGROS_FITNESSCENTER_AQUABASILEA_BUTTON_VALUE, HTML_TAG_SPAN, errorHandler, WEB_BUTTON_MIGROS_FITNESSCENTER_AQUABASILEA_BUTTON_VALUE);
      aquabasileaNavigatorHelper.waitForVisibilityOfElement(WebNavigateUtil.createXPathBy(HTML_DIV_TYPE, WEB_ELEMENT_CRITERIA_FILTER_TABLE_ATTR_NAME, WEB_ELEMENT_CRITERIA_FILTER_TABLE_ATTR_VALUE), WAIT_FOR_CRITERIA_FILTER_TABLE_TO_APPEAR.toMillis());
   }
}
