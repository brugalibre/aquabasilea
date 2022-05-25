package com.aquabasilea.web.extractcourses.impl;

import com.aquabasilea.web.error.ErrorHandler;
import com.aquabasilea.web.extractcourses.model.AquabasileaCourse;
import com.aquabasilea.web.model.CourseLocation;
import com.aquabasilea.web.navigate.AquabasileaNavigatorHelper;
import com.aquabasilea.web.util.DateUtil;
import com.zeiterfassung.web.common.navigate.util.WebNavigateUtil;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.aquabasilea.web.constant.AquabasileaWebConst.*;
import static com.zeiterfassung.web.common.constant.BaseWebConst.*;
import static java.util.Objects.isNull;

public record AquabasileaCourseExtractorHelper(AquabasileaNavigatorHelper webNavigatorHelper,
                                               ErrorHandler errorHandler) {
   private static final String PLACES_AVAILABLE_PATTERN = "(([\\d]{1,2})[\\/]([\\d]{1,2}))\\s?(?:.)+";
   private static final String TIME_OF_DAY_SEPARATOR = " - ";
   private static final Logger LOG = LoggerFactory.getLogger(AquabasileaCourseExtractorHelper.class);

   /**
    * Reads the {@link WebElement} which represents a single course and maps them into a {@link AquabasileaCourse}
    *
    * @param courseButton the {@link WebElement}-button which represents a course
    * @return a {@link AquabasileaCourse}
    */
   public AquabasileaCourse evalCourseDetailsAndCreateAquabasileaCourse(WebElement courseButton) {
      openCourseDetailDialogAndAwaitReadiness(courseButton);
      LOG.info("Evaluate AquabasileaCourse for button '{}'", courseButton.getText());
      AquabasileaCourse aquabasileaCourse = evalCourseDetailsAndCreateAquabasileaCourse();
      LOG.info("Evaluated AquabasileaCourse '{}'" , aquabasileaCourse);
      return aquabasileaCourse;
   }

   private AquabasileaCourse evalCourseDetailsAndCreateAquabasileaCourse() {
      WebElement courseDetailDialog = webNavigatorHelper.getElement(WebNavigateUtil.createXPathBy(HTML_DIV_TYPE, WEB_ELEMENT_BOOK_DIALOG_ATTR_NAME, WEB_ELEMENT_BOOK_DIALOG_ATTR_VALUE));
      WebElement dialogH1WebElement = webNavigatorHelper.findWebElementBy(courseDetailDialog, By.tagName(HTML_TAG_H1)).get();
      String courseName = dialogH1WebElement.getText();
      List<String> courseDetailsText = courseDetailElements(courseDetailDialog);
      closeCourseDetailsDialog(courseDetailDialog);
      return createAquabasileaCourse(courseName, courseDetailsText);
   }

   private void closeCourseDetailsDialog(WebElement courseDetailDialog) {
      WebElement cancelBookingButton = webNavigatorHelper.getWebElementByTageNameAndInnerHtmlValue(courseDetailDialog, HTML_BUTTON_TYPE, WEB_ELEMENT_CLOSE_BOOK_COURSE_BUTTON_TEXT);
      webNavigatorHelper.clickButton(cancelBookingButton, errorHandler);
   }

   private static AquabasileaCourse createAquabasileaCourse(String courseName, List<String> courseDetailsText) {
      String timeOfTheDay = null;
      CourseLocation courseLocation = null;
      LocalDate courseDate = null;
      for (String courseDetailSpanText : courseDetailsText) {
         if (courseDetailSpanText.contains(TIME_OF_DAY_SEPARATOR)) {
            timeOfTheDay = courseDetailSpanText.substring(0, courseDetailSpanText.indexOf(TIME_OF_DAY_SEPARATOR));
         } else if (DateUtil.isStartsWithDayOfWeek(courseDetailSpanText, Locale.GERMAN)) {
            courseDate = DateUtil.getLocalDateFromInput(courseDetailSpanText, Locale.GERMAN);
         } else if (isNull(courseLocation)) {
            courseLocation = CourseLocation.forCourseLocationName(courseDetailSpanText);
         }
      }
      return new AquabasileaCourse(courseDate, timeOfTheDay, courseLocation, courseName);
   }

   private void openCourseDetailDialogAndAwaitReadiness(WebElement courseDetails) {
      webNavigatorHelper.clickButton(courseDetails, errorHandler);
      webNavigatorHelper.waitForVisibilityOfElement(WebNavigateUtil.createXPathBy(HTML_DIV_TYPE, WEB_ELEMENT_BOOK_DIALOG_ATTR_NAME, WEB_ELEMENT_BOOK_DIALOG_ATTR_VALUE), 10000);
   }

   private List<String> courseDetailElements(WebElement courseDetailDialog) {
      List<WebElement> courseButtons = webNavigatorHelper.findAllWebElementsByPredicateAndBy(courseDetailDialog, By.tagName(HTML_TAG_P), webElement -> true);
      return courseButtons.stream()
              .map(WebElement::getText)
              .filter(StringUtils::isNotEmpty)
              .filter(value -> !value.matches(PLACES_AVAILABLE_PATTERN))
              .distinct()
              .collect(Collectors.toList());
   }
}
