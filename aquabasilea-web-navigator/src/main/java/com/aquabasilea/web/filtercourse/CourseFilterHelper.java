package com.aquabasilea.web.filtercourse;

import com.aquabasilea.web.error.ErrorHandler;
import com.aquabasilea.web.filtercourse.filter.CourseFilter;
import com.aquabasilea.web.filtercourse.filter.CourseFilterCriterion;
import com.aquabasilea.web.filtercourse.filter.FilterType;
import com.aquabasilea.web.navigate.AquabasileaNavigatorHelper;
import com.zeiterfassung.web.common.navigate.util.WebNavigateUtil;
import org.openqa.selenium.WebElement;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Optional;

import static com.aquabasilea.web.constant.AquabasileaWebConst.*;
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
      for (CourseFilterCriterion courseFilterCriterion : courseFilter.getCourseFilterCriteria()) {
         WebElement filterArea = aquabasileaNavigatorHelper.getWebElementByNameTagNameAndValue(null, HTML_DIV_TYPE, WEB_ELEMENT_CRITERIA_FILTER_TABLE_ATTR_NAME, WEB_ELEMENT_CRITERIA_FILTER_TABLE_ATTR_VALUE);
         applyFilterCriterion(errorHandler, filterArea, courseFilterCriterion);
         aquabasileaNavigatorHelper.waitForVisibilityOfElement(WebNavigateUtil.createXPathBy(HTML_DIV_TYPE, WEB_ELEMENT_CRITERIA_FILTER_TABLE_ATTR_NAME, WEB_ELEMENT_CRITERIA_FILTER_TABLE_ATTR_VALUE), WAIT_FOR_CRITERIA_FILTER_TABLE_TO_APPEAR.toMillis());
      }
      this.aquabasileaNavigatorHelper.waitForVisibilityOfElement(WebNavigateUtil.createXPathBy(HTML_DIV_TYPE, WEB_ELEMENT_COURSE_RESULTS_CONTENT_ATTR_NAME, WEB_ELEMENT_COURSE_RESULTS_CONTENT_ATTR_VALUE), 20000);
   }

   /**
    * Applies a filter criterion
    * First expand the specific filter section. then clicks on the criterion, matching our filter
    */
   private void applyFilterCriterion(ErrorHandler errorHandler, WebElement filterArea, CourseFilterCriterion courseFilterCriterion) {
      FilterType filterType = courseFilterCriterion.getFilterType();
      Optional<WebElement> courseCriterionFilterButtonOpt = aquabasileaNavigatorHelper.findParentWebElement4ChildTagNameAndInnerHtmlValue(filterArea, HTML_TAG_SPAN, filterType.getUiElementText(), HTML_BUTTON_TYPE);
      aquabasileaNavigatorHelper.clickButtonOrHandleError(courseCriterionFilterButtonOpt, () -> aquabasileaNavigatorHelper.findParentWebElement4ChildTagNameAndInnerHtmlValue(null, HTML_TAG_SPAN, filterType.getUiElementText(), HTML_BUTTON_TYPE), errorHandler, filterType.getUiElementText());

      Optional<WebElement> checkBoxForCourseNameOpt = aquabasileaNavigatorHelper.findWebElementBy(filterArea, WebNavigateUtil.createStartsWithXPathBy(HTML_TAG_INPUT, HTML_VALUE_ATTR, courseFilterCriterion.getFilterValue()));
      aquabasileaNavigatorHelper.clickButtonOrHandleError(checkBoxForCourseNameOpt, () -> aquabasileaNavigatorHelper.findWebElementBy(filterArea, filterType.createXPath(HTML_TAG_INPUT, HTML_VALUE_ATTR, courseFilterCriterion.getFilterValue())), errorHandler, courseFilterCriterion.getFilterValue());

      // Click the filter-criterion button again -> collapse filter criteria and apply the filter
      courseCriterionFilterButtonOpt = aquabasileaNavigatorHelper.findParentWebElement4ChildTagNameAndInnerHtmlValue(filterArea, HTML_TAG_SPAN, filterType.getUiElementText(), HTML_BUTTON_TYPE);
      aquabasileaNavigatorHelper.clickButtonOrHandleError(courseCriterionFilterButtonOpt, () -> aquabasileaNavigatorHelper.findParentWebElement4ChildTagNameAndInnerHtmlValue(null, HTML_TAG_SPAN, filterType.getUiElementText(), HTML_BUTTON_TYPE), errorHandler, filterType.getUiElementText());
   }
}
