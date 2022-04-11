package com.aquabasilea.web.filtercourse.filter;

import com.zeiterfassung.web.common.navigate.util.WebNavigateUtil;
import org.openqa.selenium.By;

/**
 * Describes different possible filters which can be set
 */
public enum FilterType {
   COURSE_NAME("Kurstitel"),
   DAY_OF_WEEK("Wochentage");

   private final String uiElementText;

   FilterType(String uiElementText) {
      this.uiElementText = uiElementText;
   }

   public String getUiElementText() {
      return uiElementText;
   }

   /**
    * @return a filter specific {@link By#xpath(String)} which matches best for the given filter
    */
   public By createXPath(String tagName, String attrName, String attrValue) {
      switch (this) {
         case COURSE_NAME:
            return WebNavigateUtil.createStartsWithXPathBy(tagName, attrName, attrValue);
         case DAY_OF_WEEK:
            // the webpage appends the amount of matches for the given course name.
            // In the config file, the user should not bother about that.
            return WebNavigateUtil.createXPathBy(tagName, attrName, attrValue);
      }
      return null;
   }
}
