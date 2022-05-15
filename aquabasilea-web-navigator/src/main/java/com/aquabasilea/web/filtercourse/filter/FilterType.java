package com.aquabasilea.web.filtercourse.filter;

import com.zeiterfassung.web.common.navigate.util.WebNavigateUtil;
import org.openqa.selenium.By;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Describes different possible filters which can be set
 */
public enum FilterType {

   COURSE_NAME(FilterType.COURSE_TITLE),
   COURSE_LOCATION(FilterType.CENTER, FilterType.FITNESSPARK, FilterType.FITNESSCENTER),
   DAY_OF_WEEK(FilterType.WEEK_DAYS);

   private static final String CENTER = "Center";
   private static final String FITNESSCENTER = "Fitnesscenter";
   private static final String FITNESSPARK = "Fitnesspark";
   private static final String WEEK_DAYS = "Wochentage";
   private static final String COURSE_TITLE = "Kurstitel";

   private final String uiElementText;
   private final List<String> additionallyFilterCriteriaNames;

   FilterType(String uiElementText) {
      this.uiElementText = uiElementText;
      this.additionallyFilterCriteriaNames = Collections.emptyList();
   }
   FilterType(String uiElementText, String... additionallyUiElementTextArray) {
      this.uiElementText = uiElementText;
      this.additionallyFilterCriteriaNames = Arrays.asList(additionallyUiElementTextArray);
   }

   public String getUiElementText() {
      return uiElementText;
   }

   public List<String> getAdditionallyFilterCriteriaNames() {
      return additionallyFilterCriteriaNames;
   }

   /**
    * @return a filter specific {@link By#xpath(String)} which matches best for the given filter
    */
   public By createXPath(String tagName, String attrName, String attrValue) {
      switch (this) {
         case COURSE_NAME:
            // the webpage appends the amount of matches for the given course name.
            // We don't care so ignore
            return WebNavigateUtil.createStartsWithXPathBy(tagName, attrName, attrValue);
         case DAY_OF_WEEK:
         case COURSE_LOCATION:// fall through
            return WebNavigateUtil.createXPathBy(tagName, attrName, attrValue);
      }
      return null;
   }
}
