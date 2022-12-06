package com.aquabasilea.migrosapi.service;

public class MigrosApiConst {
   public static final String BEARER = "Bearer";
   public static final String TOKEN = BEARER + " eyJraWQiOiJveHJHbU5xNFFocW1Ga1pZV0t0djhnIiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiJGMEJGMzdEQy1FREQxLTQ0MjItOEIwRC1BMjZBNkIxQkU2Q0IiLCJhY3IiOiJ1cm46YWNyOnNmYSIsImF1ZCI6WyJmaXRuZXNzY2VudGVyIiwiZnJlaXplaXQiXSwiYXpwIjoiZml0bmVzc2NlbnRlcl96N2E5ZkZrTlFtcXRLQkdOOTdBV0JnIiwic2NvcGUiOlsib3BlbmlkIiwicHJvZmlsZSIsImVtYWlsIl0sImlzcyI6Imh0dHBzOi8vbG9naW4ubWlncm9zLmNoIiwicGlkIjoiRGEyV2tDYTd4Nmw1UGNQRnUzUmQ3eiIsImV4cCI6MTY3MDE3NjM1NywiaWF0IjoxNjcwMTc1NDU3LCJqdGkiOiJTSzZoS3pkRlRsNjU2bHVZa202S29nIn0.Q3HC7KJGq9pHCq4R2glBiSIidZrboANma14zsabUgQLB325JbDDMMqVJVRfea7KUlUc3Ih84Uvar2kovWQvoI0djwXiU08EDfUlWhzwVYcO04O5PKdwhi-J5kdZCh6kBF2Q9yrKxiwqnyK6zNBixJVPMPBekI6QMiy1tgHgOiWEeNnc0alIsTT6IDW8g_0dlU2uJfzozFlijRsfgI1REvEMCiE_Q3B2joNhqvPSBeiAbpL2He7Q0HjM77Ktrpsv1VkAkm4gSXeOoGmanDOjeUVVd3HJo3f820TZdg2fI3OW3U5Xbn1hrkgA2BvQipwi2n4FQPorzH71Xxo3q8jXQyA";

   public static final String TAKE_PLACEHOLDER = "$takePlaceholder";
   public static final String CENTER_IDS_PLACEHOLDER = "$centerIds";
   public static final String WEEK_DAY_PLACEHOLDER = "$weekDay";
   public static final String COURSE_TITLES_PLACEHOLDER = "$courseTitles";
   public static final String MIGROS_GET_COURSES_REQUEST_BODY = "{\"language\":\"de\",\"skip\":0,\"take\":" + TAKE_PLACEHOLDER + ",\"selectMethod\":2,\"memberIdTac\":0,\"centerIds\":[" + CENTER_IDS_PLACEHOLDER + "],\"daytimeIds\":[],\"weekdayIds\":[" + WEEK_DAY_PLACEHOLDER + "],\"coursetitles\":[" + COURSE_TITLES_PLACEHOLDER + "]}";

   public static final String CENTER_ID_PLACEHOLDER = "$centerId";
   public static final String COURSE_ID_TAC_PLACEHOLDER = "$courseIdTac";
   public static final String MIGROS_BOOK_COURSE_REQUEST_BODY = "{\"language\":\"de\",\"centerId\":" + CENTER_ID_PLACEHOLDER + ",\"courseIdTac\":" + COURSE_ID_TAC_PLACEHOLDER + "}";
   public static final String MIGROS_BOOKING_URL = "https://blfa-api.migros.ch/kp/api/Booking?";
   public static final String MIGROS_GET_COURSES_URL = "https://blfa-api.migros.ch/kp/api/Courselist/all?";
}
