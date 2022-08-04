package com.aquabasilea.web.constant;

import java.time.Duration;

public class AquabasileaWebConst {

   // Time outs
   public static final int DEFAULT_TIMEOUT = 400;
   public static final Duration WAIT_FOR_CRITERIA_FILTER_TABLE_TO_APPEAR = Duration.ofMillis(150000);
   public static final Duration WAIT_FOR_CRITERIA_FILTER_TO_BE_APPLIED = Duration.ofMillis(7000);
   /**
    * The time a refresh of the page takes
    */
   public static final Duration PAGE_REFRESH_DURATION = Duration.ofSeconds(10);

   public static final Duration WAIT_UNTIL_LOADING_ANIMATION_DISAPPEARS = Duration.ofSeconds(10);
   public static final String LOADING_ANIMATION_CLASS_NAME = "sc-dkmKIT iBFRfU";

   public static final String COURSE_PAGE = "coursePage";
   public static final String AQUABASILEA_WEB_KURS_BUCHER_PROPERTIES = "config/aquabasilea-kurs-bucher-config.yml";
   public static final String HTML_VALUE_ATTR = "value";

   // Filter fields
   public static final String WEB_ELEMENT_CRITERIA_FILTER_TABLE_ATTR_VALUE = "filter";
   public static final String WEB_ELEMENT_CRITERIA_FILTER_TABLE_ATTR_NAME = "data-test-id";

   public static final String WEB_BUTTON_MIGROS_FITNESSCENTER_AQUABASILEA_BUTTON_VALUE = "Migros Fitnesscenter Aquabasilea";
   public static final String WEB_BUTTON_FILTER_RESULTS_SHOW_MORE_VALUE = "Mehr anzeigen";
   public static final String WEB_BUTTON_COURSE_LOCATION_FILTER_BUTTON_VALUE = "Center";
   public static final String WEB_BUTTON_FITNESSCENTER_FILTER_BUTTON_VALUE = "Fitnesscenter";
   public static final String WEB_BUTTON_FITNESSPARK_FILTER_BUTTON_VALUE = "Fitnesspark";
   public static final String WEB_BUTTON_MIGROS_FITNESSCENTER_AQUABASILEA_CRITERION_BUTTON_VALUE = "Migros Fitnesscenter Aquabasilea";

   // Course results field
   public static final String WEB_ELEMENT_COURSE_RESULTS_CONTENT_ATTR_VALUE = "course-table";
   public static final String WEB_ELEMENT_COURSE_RESULTS_CONTENT_ATTR_NAME = "data-test-id";

   // Book a place
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

   // on select-course page
   public static final String WEB_ELEMENT_LOGIN_SELECT_COURSE_ANMELDE_BUTTON_ATTR_ID_TEXT = "Jetzt einloggen";
   public static final String WEB_ELEMENT_LOGIN_SELECT_COURSE_ABMELDEN_BUTTON_ATTR_ID_TEXT = "Abmelden";
   public static final String WEB_ELEMENT_LOGIN_SELECT_COURSE_ANMELDE_BUTTON_ATTR_ID = "aria-label";

   private AquabasileaWebConst() {
      // privé
   }
}
