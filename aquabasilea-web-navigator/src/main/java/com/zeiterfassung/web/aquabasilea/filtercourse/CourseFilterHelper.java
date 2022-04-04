package com.zeiterfassung.web.aquabasilea.filtercourse;

import com.zeiterfassung.web.aquabasilea.error.ErrorHandler;
import com.zeiterfassung.web.aquabasilea.filtercourse.filter.CourseFilter;
import com.zeiterfassung.web.aquabasilea.filtercourse.filter.CourseFilterCriterion;
import com.zeiterfassung.web.aquabasilea.filtercourse.filter.FilterType;
import com.zeiterfassung.web.aquabasilea.navigate.AquabasileaNavigatorHelper;
import com.zeiterfassung.web.common.navigate.util.WebNavigateUtil;
import org.openqa.selenium.WebElement;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Optional;

import static com.zeiterfassung.web.aquabasilea.constant.AquabasileaWebConst.*;
import static com.zeiterfassung.web.common.constant.BaseWebConst.*;

public class CourseFilterHelper {
   private AquabasileaNavigatorHelper aquabasileaNavigatorHelper;

   public CourseFilterHelper(AquabasileaNavigatorHelper aquabasileaNavigatorHelper) {
      this.aquabasileaNavigatorHelper = aquabasileaNavigatorHelper;
   }

   /**
    * Builds a {@link CourseFilter} and applies those filters to the aquabasilea-course filter
    *
    * @param courseName   the name of the course
    * @param dayOfWeek    the day, on which the course takes place
    * @param errorHandler the {@link ErrorHandler} to handle errors
    */
   public void applyCriteriaFilter(String courseName, DayOfWeek dayOfWeek, ErrorHandler errorHandler) {
      applyFilterCriteria(CourseFilter.CourseFilterBuilder.builder()
              .addCourseFilterCriterion(CourseFilterCriterion.of(FilterType.COURSE_NAME, courseName))
              .addCourseFilterCriterion(CourseFilterCriterion.of(FilterType.DAY_OF_WEEK, dayOfWeek.getDisplayName(TextStyle.FULL, Locale.GERMAN)))
              .build(), errorHandler);
   }

   private void applyFilterCriteria(CourseFilter courseFilter, ErrorHandler errorHandler) {
      WebNavigateUtil.waitForMilliseconds(DEFAULT_TIMEOUT);
      WebElement filterArea = aquabasileaNavigatorHelper.getWebElementByNameTagNameAndValue(null, HTML_DIV_TYPE, WEB_ELEMENT_CRITERIA_FILTER_TABLE_ATTR_NAME, WEB_ELEMENT_CRITERIA_FILTER_TABLE_ATTR_VALUE);
      for (CourseFilterCriterion courseFilterCriterion : courseFilter.getCourseFilterCriteria()) {
         applyFilterCriterion(errorHandler, filterArea, courseFilterCriterion);
      }
      clickApplyFilterButton(errorHandler);
   }

   private void clickApplyFilterButton(ErrorHandler errorHandler) {
      // we have to get the filter-area again, since the underlying page may have been changed -> StaleElementReferenceException
      aquabasileaNavigatorHelper.waitForVisibilityOfElement(WebNavigateUtil.createXPathBy(HTML_DIV_TYPE, WEB_ELEMENT_CRITERIA_FILTER_TABLE_ATTR_NAME, WEB_ELEMENT_CRITERIA_FILTER_TABLE_ATTR_VALUE), 10000);
      WebElement filterArea = aquabasileaNavigatorHelper.getWebElementByNameTagNameAndValue(null, HTML_DIV_TYPE, WEB_ELEMENT_CRITERIA_FILTER_TABLE_ATTR_NAME, WEB_ELEMENT_CRITERIA_FILTER_TABLE_ATTR_VALUE);
      Optional<WebElement> applyFilterButtonOpt = aquabasileaNavigatorHelper.findParentWebElement4ChildTagNameAndInnerHtmlValue(filterArea, HTML_TAG_SPAN, WEB_ELEMENT_APPLY_FILTER_BUTTON_TEXT, HTML_BUTTON_TYPE);
      aquabasileaNavigatorHelper.clickButtonOrHandleError(applyFilterButtonOpt, () -> aquabasileaNavigatorHelper.findParentWebElement4ChildTagNameAndInnerHtmlValue(null, HTML_TAG_SPAN, WEB_ELEMENT_APPLY_FILTER_BUTTON_TEXT, HTML_BUTTON_TYPE), errorHandler, WEB_ELEMENT_APPLY_FILTER_BUTTON_TEXT);

      // wait until the filter is applied and the results displayed
      aquabasileaNavigatorHelper.waitForVisibilityOfElement(WebNavigateUtil.createXPathBy(HTML_DIV_TYPE, WEB_ELEMENT_COURSE_RESULTS_CONTENT_ATTR_NAME, WEB_ELEMENT_COURSE_RESULTS_CONTENT_ATTR_VALUE), 20000);
   }

   /**
    * Applies a filter criterion
    * First expand the specific filter section. then clicks on the criterion, matching our filter
    */
   private void applyFilterCriterion(ErrorHandler errorHandler, WebElement filterArea, CourseFilterCriterion courseFilterCriterion) {
      FilterType filterType = courseFilterCriterion.getFilterType();
      Optional<WebElement> courseCriterionFilterButtonOpt = aquabasileaNavigatorHelper.findParentWebElement4ChildTagNameAndInnerHtmlValue(null, HTML_TAG_SPAN, filterType.getUiElementText(), HTML_BUTTON_TYPE);
      aquabasileaNavigatorHelper.clickButtonOrHandleError(courseCriterionFilterButtonOpt, () -> aquabasileaNavigatorHelper.findParentWebElement4ChildTagNameAndInnerHtmlValue(null, HTML_TAG_SPAN, filterType.getUiElementText(), HTML_BUTTON_TYPE), errorHandler, filterType.getUiElementText());

      Optional<WebElement> checkBoxForCourseNameOpt = aquabasileaNavigatorHelper.findWebElementBy(filterArea, WebNavigateUtil.createStartsWithXPathBy(HTML_TAG_INPUT, HTML_VALUE_ATTR, courseFilterCriterion.getFilterValue()));
      aquabasileaNavigatorHelper.clickButtonOrHandleError(checkBoxForCourseNameOpt, () -> aquabasileaNavigatorHelper.findWebElementBy(filterArea, filterType.createXPath(HTML_TAG_INPUT, HTML_VALUE_ATTR, courseFilterCriterion.getFilterValue())), errorHandler, courseFilterCriterion.getFilterValue());
   }
}
