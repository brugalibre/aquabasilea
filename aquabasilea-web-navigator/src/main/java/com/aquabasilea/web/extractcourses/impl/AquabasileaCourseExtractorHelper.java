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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.aquabasilea.web.constant.AquabasileaWebConst.*;
import static com.zeiterfassung.web.common.constant.BaseWebConst.*;
import static java.util.Objects.*;

public record AquabasileaCourseExtractorHelper(AquabasileaNavigatorHelper webNavigatorHelper,
                                               ErrorHandler errorHandler) {
   private static final String COURSE_INSTRUCTOR_PATTERN = "(^[a-zA-Z]+[ ][A-Z]([.]$))";
   private static final String PLACES_AVAILABLE_PATTERN = "(([\\d]{1,2})[\\/]([\\d]{1,2}))\\s?(?:.)+";
   private static final String TIME_OF_DAY_SEPARATOR = " - ";
   private static final Logger LOG = LoggerFactory.getLogger(AquabasileaCourseExtractorHelper.class);

   /**
    * Reads all the {@link WebElement}s which represents courses and converts them into a list of {@link AquabasileaCourse}s
    *
    * @return a {@link List} of {@link AquabasileaCourse}s
    */
   public List<AquabasileaCourse> findAndMapAllAquabasileaCourseButtons() {
      List<WebElement> courseButtons = findAllAquabasileaCourseButtons();
      return map2AquabasileaCourses(courseButtons);
   }

   /**
    * Reads all the {@link WebElement}s which represents courses
    *
    * @return a {@link List} of migros-courses as {@link WebElement}s
    */
   public List<WebElement> findAllAquabasileaCourseButtons() {
      WebElement courseArea = webNavigatorHelper.findWebElementBy(null, WebNavigateUtil.createXPathBy(HTML_DIV_TYPE, WEB_ELEMENT_COURSE_RESULTS_CONTENT_ATTR_NAME, WEB_ELEMENT_COURSE_RESULTS_CONTENT_ATTR_VALUE)).get();
      return webNavigatorHelper.findAllWebElementsByPredicateAndBy(courseArea, By.tagName(HTML_BUTTON_TYPE), webElement -> true);
   }

   private List<AquabasileaCourse> map2AquabasileaCourses(List<WebElement> courseButtons) {
      return courseButtons.stream()
              .map(this::evalCourseDetailsAndCreateAquabasileaCourse)
              .collect(Collectors.toList());
   }

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
      LOG.info("Evaluated AquabasileaCourse '{}'", aquabasileaCourse);
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
      String courseInstructor = "";
      CourseLocation courseLocation = null;
      LocalDate courseDate = null;
      for (String courseDetailSpanText : courseDetailsText) {
         if (courseDetailSpanText.contains(TIME_OF_DAY_SEPARATOR) && isNull(timeOfTheDay)) {
            timeOfTheDay = courseDetailSpanText.substring(0, courseDetailSpanText.indexOf(TIME_OF_DAY_SEPARATOR));
         } else if (courseDetailSpanText.matches(COURSE_INSTRUCTOR_PATTERN)) {
            courseInstructor = courseDetailSpanText;
         } else if (DateUtil.isStartsWithDayOfWeek(courseDetailSpanText, Locale.GERMAN)) {
            courseDate = DateUtil.getLocalDateFromInput(courseDetailSpanText, Locale.GERMAN);
         } else if (isNull(courseLocation)) {
            courseLocation = CourseLocation.forCourseLocationName(courseDetailSpanText);
         } else if (nonNull(courseDate) && StringUtils.isNotEmpty(courseInstructor) && nonNull(timeOfTheDay)) {
            break;// we're done so far
         }
      }
      requireNonNull(courseDate, "CourseDate konnte nicht ermittelt werden! courseDetailsText:" + courseDetailsText);
      LocalTime localTime = DateUtil.getLocalTimeFromInput(timeOfTheDay);
      return new AquabasileaCourse(LocalDateTime.of(courseDate, localTime), courseLocation, courseName, courseInstructor);
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
