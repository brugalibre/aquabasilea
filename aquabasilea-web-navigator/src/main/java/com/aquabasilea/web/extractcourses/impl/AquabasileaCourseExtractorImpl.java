package com.aquabasilea.web.extractcourses.impl;

import com.aquabasilea.web.error.ErrorHandlerImpl;
import com.aquabasilea.web.extractcourses.AquabasileaCourseExtractor;
import com.aquabasilea.web.extractcourses.model.AquabasileaCourse;
import com.aquabasilea.web.extractcourses.model.ExtractedAquabasileaCourses;
import com.aquabasilea.web.filtercourse.CourseFilterHelper;
import com.aquabasilea.web.filtercourse.filter.CourseFilterCriterion;
import com.aquabasilea.web.filtercourse.filter.FilterType;
import com.aquabasilea.web.model.CourseLocation;
import com.aquabasilea.web.navigate.AbstractAquabasileaWebNavigator;
import com.zeiterfassung.web.common.navigate.util.WebNavigateUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import static com.aquabasilea.web.constant.AquabasileaWebConst.*;
import static com.zeiterfassung.web.common.constant.BaseWebConst.HTML_BUTTON_TYPE;
import static com.zeiterfassung.web.common.constant.BaseWebConst.HTML_DIV_TYPE;

public class AquabasileaCourseExtractorImpl extends AbstractAquabasileaWebNavigator implements AquabasileaCourseExtractor {

   private static final Logger LOG = LoggerFactory.getLogger(AquabasileaCourseExtractorImpl.class);
   private final ErrorHandlerImpl errorHandler;
   private int timeOutRetries;
   private AquabasileaCourseExtractorHelper aquabasileaCourseExtractorHelper;

   public AquabasileaCourseExtractorImpl(String propertiesName) {
      super("", new char[]{}, propertiesName);
      this.errorHandler = new ErrorHandlerImpl();
      this.timeOutRetries = 3;
   }

   @Override
   public void initWebDriver() {
      super.initWebDriver();
      this.aquabasileaCourseExtractorHelper = new AquabasileaCourseExtractorHelper(this.webNavigatorHelper, this.errorHandler);
   }

   public static AquabasileaCourseExtractor createAndInitAquabasileaWebNavigator() {
      AquabasileaCourseExtractorImpl aquabasileaWebNavigator = new AquabasileaCourseExtractorImpl(AQUABASILEA_WEB_KURS_BUCHER_PROPERTIES);
      aquabasileaWebNavigator.initWebDriver();
      return aquabasileaWebNavigator;
   }

   @Override
   public ExtractedAquabasileaCourses extractAquabasileaCourses(List<CourseLocation> courseLocations) {
      try {
         return extractAquabasileaCoursesInternal(courseLocations);
      } catch (TimeoutException e) {
         return handleTimeoutException(courseLocations, e);
      }
   }

   private ExtractedAquabasileaCourses extractAquabasileaCoursesInternal(List<CourseLocation> courseLocations) {
      LOG.info("Start extracting courses for locations {} ", courseLocations);
      navigate2CoursePageAndAwaitReadiness();
      filterAndShowCourses(courseLocations);
      List<AquabasileaCourse> aquabasileaCourses = aquabasileaCourseExtractorHelper.findAndMapAllAquabasileaCourseButtons();
      logout();
      LOG.info("Done extracting courses, found {} courses ", aquabasileaCourses.size());
      return () -> aquabasileaCourses;
   }

   /**
    * Defines the necessary filter criteria and shows all found courses
    */
   private void filterAndShowCourses(List<CourseLocation> courseLocations) {
      CourseFilterHelper courseFilterHelper = new CourseFilterHelper(webNavigatorHelper);
      List<String> courseLocationsAsString = courseLocations.stream().map(CourseLocation::getCourseLocationName).toList();
      List<CourseFilterCriterion> criteria = List.of(CourseFilterCriterion.of(FilterType.COURSE_LOCATION, courseLocationsAsString));
      courseFilterHelper.applyCriteriaFilter(criteria, errorHandler);
      showAllSearchResults();
   }

   private void showAllSearchResults() {
      waitForVisibilityOfElement(WebNavigateUtil.createXPathBy(HTML_DIV_TYPE, WEB_ELEMENT_COURSE_RESULTS_CONTENT_ATTR_NAME, WEB_ELEMENT_COURSE_RESULTS_CONTENT_ATTR_VALUE), WAIT_FOR_CRITERIA_FILTER_TABLE_TO_APPEAR.toMillis());
      webNavigatorHelper.findWebElementByTageNameAndInnerHtmlValue(null, HTML_BUTTON_TYPE, WEB_BUTTON_FILTER_RESULTS_SHOW_MORE_VALUE)
              .ifPresent(showMoreButton -> {
                 // Sometimes this button is not visible since all results are already displayed
                 webNavigatorHelper.clickButton(showMoreButton, errorHandler);
                 webNavigatorHelper.waitForInvisibilityOfElement(showMoreButton);
              });
   }

   private void navigate2CoursePageAndAwaitReadiness() {
      navigateToPage(coursePage);
      waitForVisibilityOfElement(WebNavigateUtil.createXPathBy(HTML_DIV_TYPE, WEB_ELEMENT_CRITERIA_FILTER_TABLE_ATTR_NAME, WEB_ELEMENT_CRITERIA_FILTER_TABLE_ATTR_VALUE), WAIT_FOR_CRITERIA_FILTER_TABLE_TO_APPEAR.toMillis());
   }

   private ExtractedAquabasileaCourses handleTimeoutException(List<CourseLocation> courseLocations, TimeoutException e) {
      if (timeOutRetries > 0) {
         handlingError(String.format("Timeout while extracting courses. Retries left: %s", this.timeOutRetries), e);
         timeOutRetries--;
         return extractAquabasileaCourses(courseLocations);
      }
      handlingError("Unrecoverable timeout while extracting courses!", e);
      throw e;
   }

   private void handlingError(String errorLogMsg, TimeoutException e) {
      LOG.error(errorLogMsg, e);
      webNavigatorHelper.takeScreenshot("extracting_courses_" + e.getClass().getSimpleName());
   }
}
