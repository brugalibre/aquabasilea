package com.aquabasilea.web.constant;

import com.zeiterfassung.web.common.inout.PropertyReader;

import java.time.Duration;

public class AquabasileaWebConst {

   // Time outs
   public static final int DEFAULT_TIMEOUT = 400;
   public static final Duration WAIT_FOR_CRITERIA_FILTER_TABLE_TO_APPEAR = Duration.ofMillis(220_000);

   private static final Duration WAIT_FOR_COURSE_TABLE_TO_APPEAR = Duration.ofSeconds(40);
   private static final Duration WAIT_FOR_BOOK_DIALOG_TO_APPEAR = Duration.ofSeconds(15);
   private static final Duration PAGE_REFRESH_DURATION = Duration.ofSeconds(13);
   private static final Duration WAIT_UNTIL_LOADING_ANIMATION_DISAPPEARS = Duration.ofSeconds(40);

   public static final String LOADING_ANIMATION_CLASS_NAME = "sc-dkmKIT iBFRfU";

   public static final String COURSE_PAGE = "coursePage";
   public static final String AQUABASILEA_WEB_KURS_BUCHER_PROPERTIES = "config/aquabasilea-kurs-bucher-config.yml";
   public static final String HTML_VALUE_ATTR = "value";

   // Filter fields
   public static final String WEB_ELEMENT_CRITERIA_FILTER_TABLE_ATTR_VALUE = "filter";
   public static final String WEB_ELEMENT_CRITERIA_FILTER_TABLE_ATTR_NAME = "data-test-id";

   public static final String WEB_BUTTON_MIGROS_FITNESSCENTER_AQUABASILEA_BUTTON_VALUE = "Migros Fitnesscenter Aquabasilea";
   public static final String WEB_BUTTON_FILTER_RESULTS_SHOW_MORE_VALUE = "Mehr anzeigen";
   public static final String WEB_BUTTON_CLEAR_ALL_FILTERS = "Alle Filter zurücksetzen";
   public static final String WEB_BUTTON_COURSE_LOCATION_FILTER_BUTTON_VALUE = "Center";
   public static final String WEB_BUTTON_FITNESSCENTER_FILTER_BUTTON_VALUE = "Fitnesscenter";
   public static final String WEB_BUTTON_FITNESSPARK_FILTER_BUTTON_VALUE = "Fitnesspark";
   public static final String WEB_BUTTON_MIGROS_FITNESSCENTER_AQUABASILEA_CRITERION_BUTTON_VALUE = "Migros Fitnesscenter Aquabasilea";

   // Course results field
   public static final String WEB_ELEMENT_COURSE_RESULTS_CONTENT_ATTR_VALUE = "course-table";
   public static final String WEB_ELEMENT_COURSE_RESULTS_CONTENT_ATTR_NAME = "data-test-id";
   public static final String WEB_ELEMENT_SELECTED_COURSE_FILTERS_PREFIX_ATTR_NAME = "selected-filters-list-";

   // Book a place
   public static final String WEB_ELEMENT_COURSE_ALREADY_BOOKED_VALUE = "Platz leider ausgebucht";
   public static final String WEB_ELEMENT_BOOK_SPOT_BUTTON_TEXT = "Platz buchen";
   public static final String WEB_ELEMENT_CANCEL_SPOT_BUTTON_TEXT = "Platz stornieren";
   public static final String WEB_ELEMENT_CLOSE_BOOK_COURSE_BUTTON_TEXT = "schliessen";
   public static final String WEB_ELEMENT_BOOK_DIALOG_ATTR_NAME = "data-test-id";
   public static final String WEB_ELEMENT_BOOK_DIALOG_ATTR_VALUE = "course-detail-container";

   // Login fields
   // on login page
   public static final String WEB_ELEMENT_PWD_FIELD_ID = "password";
   public static final String WEB_ELEMENT_USER_NAME_FIELD_ID = "username";
   public static final String WEB_ELEMENT_ANMELDE_BUTTON_TEXT = "ANMELDEN";
   public static final String LOGIN_FAILED_ERROR_MSG_ID = "error-message";

   public static final String MIGROS_ACOUNT_LOGIN_ICON = "a-accountloginicon__icon";
   public static final String MIGROS_ACCOUNT_TILE_PROFILE_LINK = "m-tile__profile-link";

   // on select-course page
   public static final String WEB_ELEMENT_LOGIN_SELECT_COURSE_ANMELDE_BUTTON_ATTR_ID_TEXT = "Jetzt einloggen";
   public static final String WEB_ELEMENT_LOGIN_SELECT_COURSE_ABMELDEN_BUTTON_ATTR_ID_TEXT = "Abmelden";
   public static final String WEB_ELEMENT_LOGIN_SELECT_COURSE_ANMELDE_BUTTON_ATTR_ID = "aria-label";

   private AquabasileaWebConst() {
      // privé
   }

   /**
    * Returns the duration which the web-driver waits until a loading animation disappears. It's either the default value
    * or the configured one, using the property <code>millis_wait_until_loading_animation_disappears</code>
    *
    * @param propertyReader the {@link PropertyReader} to read the configured value from the yml/properties file
    * @return the duration which the web-driver waits until a loading animation disappears
    */
   public static Duration getWaitUntilLoadingAnimationDisappearsDuration(PropertyReader propertyReader) {
      String durationToWaitUntilLoadingAnimationDisappear = propertyReader.readValueOrDefault(
              "millis_wait_until_loading_animation_disappears", String.valueOf(WAIT_UNTIL_LOADING_ANIMATION_DISAPPEARS.toMillis()));
      return Duration.ofMillis(Integer.parseInt(durationToWaitUntilLoadingAnimationDisappear));
   }

   /**
    * Returns the duration the web-driver waits until it expects the page to be loaded. It's either the default value
    * or the configured one, using the property <code>page_refresh_duration_ms</code>
    *
    * @param propertyReader the {@link PropertyReader} to read the configured value from the yml/properties file
    * @return the duration the web-driver waits until it expects the page to be loaded
    */
   public static Duration getPageRefreshDuration(PropertyReader propertyReader) {
      String pageRefreshDuration = propertyReader.readValueOrDefault(
              "page_refresh_duration_ms", String.valueOf(PAGE_REFRESH_DURATION.toMillis()));
      return Duration.ofMillis(Integer.parseInt(pageRefreshDuration));
   }

   /**
    * Returns the duration which the web-driver waits until the course table appears, e.g. after the page was reloaded. It's either the default value
    * or the configured one, using the property <code>millis_to_wait_until_course_table_appears</code>
    *
    * @param propertyReader the {@link PropertyReader} to read the configured value from the yml/properties file
    * @return the duration which the web-driver waits until the course table appears
    */
   public static Duration getWaitForCourseTableToAppearDuration(PropertyReader propertyReader) {
      String durationToWaitUntilCourseTableAppears = propertyReader.readValueOrDefault(
              "millis_to_wait_until_course_table_appears", String.valueOf(WAIT_FOR_COURSE_TABLE_TO_APPEAR.toMillis()));
      return Duration.ofMillis(Integer.parseInt(durationToWaitUntilCourseTableAppears));
   }

   public static Duration getWaitForBookDialogToAppearDuration(PropertyReader propertyReader) {
      String durationToWaitUntilBookDialogAppears = propertyReader.readValueOrDefault(
              "millis_to_wait_until_book_dialog_appears", String.valueOf(WAIT_FOR_BOOK_DIALOG_TO_APPEAR.toMillis()));
      return Duration.ofMillis(Integer.parseInt(durationToWaitUntilBookDialogAppears));
   }
}
