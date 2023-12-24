package com.aquabasilea.migrosapi.service.config;

import static com.aquabasilea.migrosapi.service.config.MigrosApiConst.*;

public class UrlConfig {

   private final String bookCourseUrl;
   private final String getBookedCoursesUrl;
   private final String deleteBookedCoursesUrl;
   private final String migrosGetCoursesUrl;
   private final String migrosGetCentersUrl;

   /**
    * {@link UrlConfig} with all default URLs
    */
   public UrlConfig() {

      this.bookCourseUrl = BOOK_COURSE_URL;
      this.getBookedCoursesUrl = GET_BOOKED_COURSES_URL;
      this.deleteBookedCoursesUrl = DELETE_BOOKED_COURSES_URL;
      this.migrosGetCoursesUrl = MIGROS_GET_COURSES_URL;
      this.migrosGetCentersUrl = MIGROS_GET_CENTERS_URL;
   }

   public UrlConfig(String bookCourseUrl, String getBookedCoursesUrl, String deleteBookedCoursesUrl,
                    String migrosGetCoursesUrl, String migrosGetCentersUrl) {
      this.bookCourseUrl = bookCourseUrl;
      this.getBookedCoursesUrl = getBookedCoursesUrl;
      this.deleteBookedCoursesUrl = deleteBookedCoursesUrl;
      this.migrosGetCoursesUrl = migrosGetCoursesUrl;
      this.migrosGetCentersUrl = migrosGetCentersUrl;
   }

   public String getBookCourseUrl() {
      return bookCourseUrl;
   }

   public String getGetBookedCoursesUrl() {
      return getBookedCoursesUrl;
   }

   public String getDeleteBookedCoursesUrl() {
      return deleteBookedCoursesUrl;
   }

   public String getMigrosGetCoursesUrl() {
      return migrosGetCoursesUrl;
   }

   public String getMigrosGetCentersUrl() {
      return migrosGetCentersUrl;
   }

}
