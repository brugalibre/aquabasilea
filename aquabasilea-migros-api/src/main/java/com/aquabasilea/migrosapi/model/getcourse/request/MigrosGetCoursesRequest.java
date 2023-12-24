package com.aquabasilea.migrosapi.model.getcourse.request;

import com.aquabasilea.migrosapi.api.v1.model.getcourse.request.MigrosApiGetCoursesRequest;

import java.util.List;

/**
 * Defines an internal request for getting all courses for the given locations, days and or course-titles
 *
 * @param courseCenterIds the ids of the relevant course-centers
 * @param dayIds          the ids of the relevant day. Monday e.g. would be '1', Tuesday '2' and so on
 * @param courseTitles    the names of the relevant course-titles
 * @param take            the amount of relevant days into the features to consider.
 *                        E.g. today is the 6.12 and take is = '4', that means the migros-api considers courses until and incl. 9.12. With take = 5' it would be until the 10.12 and so on.
 *                        So for retrieving courses for one week, take must be <code>7</code>
 * @param onlyBooked      <code>true</code> if only booked courses should be fetched
 */
public record MigrosGetCoursesRequest(List<String> courseCenterIds, List<String> dayIds, List<String> courseTitles,
                                      String take, boolean onlyBooked) {

   public static MigrosGetCoursesRequest of(MigrosApiGetCoursesRequest migrosApiGetCoursesRequest) {
      return new MigrosGetCoursesRequest(migrosApiGetCoursesRequest.courseCenterIds(), migrosApiGetCoursesRequest.dayIds(), migrosApiGetCoursesRequest.courseTitles(), migrosApiGetCoursesRequest.take(), false);
   }

   public static MigrosGetCoursesRequest booked() {
      return new MigrosGetCoursesRequest(List.of(), List.of(), List.of(), "0", true);
   }

   /**
    * @return <code>true</code> if this request neither contains any center-ids, course-titles nor day-ids
    */
   public boolean isEmptyRequest() {
      return courseCenterIds().isEmpty()
              && courseTitles().isEmpty()
              && dayIds().isEmpty();
   }
}
