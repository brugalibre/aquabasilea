package com.aquabasilea.migrosapi.service.config;

public class MigrosApiConst {
   public static final String TAKE_PLACEHOLDER = "$takePlaceholder";
   public static final String CENTER_IDS_PLACEHOLDER = "$centerIds";
   public static final String WEEK_DAY_PLACEHOLDER = "$weekDay";
   public static final String COURSE_TITLES_PLACEHOLDER = "$courseTitles";
   public static final String MIGROS_GET_COURSES_REQUEST_BODY = "{\"language\":\"de\",\"skip\":0,\"take\":" + TAKE_PLACEHOLDER + ",\"selectMethod\":2,\"memberIdTac\":0,\"centerIds\":[" + CENTER_IDS_PLACEHOLDER + "],\"daytimeIds\":[],\"weekdayIds\":[" + WEEK_DAY_PLACEHOLDER + "],\"coursetitles\":[" + COURSE_TITLES_PLACEHOLDER + "]}";

   public static final String CENTER_ID_PLACEHOLDER = "$centerId";
   public static final String COURSE_ID_TAC_PLACEHOLDER = "$courseIdTac";
   public static final String MIGROS_BOOK_COURSE_REQUEST_BODY = "{\"language\":\"de\",\"centerId\":" + CENTER_ID_PLACEHOLDER + ",\"courseIdTac\":" + COURSE_ID_TAC_PLACEHOLDER + "}";
   public static final String BOOK_COURSE_URL = "https://web-api.migros.ch/widgets/blfa/booking_post";
   public static final String GET_BOOKED_COURSES_URL = "https://web-api.migros.ch/widgets/blfa/booking_get";
   public static final String DELETE_BOOKED_COURSES_URL = "https://web-api.migros.ch/widgets/blfa/booking_delete";
   public static final String MIGROS_GET_COURSES_URL = "https://web-api.migros.ch/widgets/blfa/courselist";
   public static final String MIGROS_GET_CENTERS_URL = "https://web-api.migros.ch/widgets/blfa/format?key=9e74726846ff4e91a515edd24618d463ae26c89e7ea907fe30db2901da3691ba";
   // we book a course 3'000ms after a course became bookable. E.g. course is scheduled at 15:15 -> we book it at 15:15 and 3s
   public static final long BOOK_COURSE_OFFSET_MS = 3000;
}
