package com.aquabasilea.migrosapi.api.v1.model.getcourse.request;

import com.aquabasilea.migrosapi.api.v1.model.getcourse.response.MigrosApiGetCoursesResponse;

import java.util.List;

/**
 * Defines a request for getting all courses for the given locations, days and or course-titles
 *
 * @param courseCenterIds the ids of the relevant course-centers
 * @param dayIds          the ids of the relevant day. Monday e.g. would be '1', Tuesday '2' and so on
 * @param courseTitles    the names of the relevant course-titles
 * @param take            the amount of relevant days into the features to consider.
 *                        E.g. today is the 6.12 and take is = '4', that means the migros-api considers courses until and incl. 9.12. With take = 5' it would be until the 10.12 and so on.
 *                        So for retrieving courses for one week, take must be <code>7</code>
 */
public record MigrosApiGetCoursesRequest(List<String> courseCenterIds, List<String> dayIds, List<String> courseTitles,
                                         String take) {

   /**
    * When nothing else is defined we search always for courses of the current week
    */
   public static final String DEFAULT_TAKE = "8";

   /**
    * Creates a new {@link MigrosApiGetCoursesResponse} with the given course-center-ids, the day-ids and the course-titles
    *
    * @param courseCenterIds the relevant center-ids
    * @param dayIds          the relevant day-ids
    * @param courseTitles    the relevant course-titles
    * @return a {@link MigrosApiGetCoursesRequest}
    */
   public static MigrosApiGetCoursesRequest of(List<String> courseCenterIds, List<String> dayIds, List<String> courseTitles) {
      return new MigrosApiGetCoursesRequest(courseCenterIds, dayIds, courseTitles, DEFAULT_TAKE);
   }

   /**
    * Creates a new {@link MigrosApiGetCoursesResponse} with the given course-center-ids and the day-ids.
    * The value of <code>take</code> will be set to {@value MigrosApiGetCoursesRequest#DEFAULT_TAKE}
    *
    * @param courseCenterIds the relevant center-ids
    * @return a {@link MigrosApiGetCoursesRequest}
    */
   public static MigrosApiGetCoursesRequest of(List<String> courseCenterIds) {
      return new MigrosApiGetCoursesRequest(courseCenterIds, List.of(), List.of(), DEFAULT_TAKE);
   }
}
