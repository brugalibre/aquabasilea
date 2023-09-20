package com.aquabasilea.service.coursebooker.bookedcourses;

import com.aquabasilea.domain.course.model.Course;
import com.aquabasilea.domain.coursebooker.AquabasileaCourseBooker;
import com.aquabasilea.domain.coursebooker.AquabasileaCourseBookerHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookedCourseHelper {
   private static final Logger LOG = LoggerFactory.getLogger(BookedCourseHelper.class);
   private final AquabasileaCourseBookerHolder aquabasileaCourseBookerHolder;
   private final Map<String, Boolean> userIdToIsFetchingBookedCoursesMap;

   public BookedCourseHelper(AquabasileaCourseBookerHolder aquabasileaCourseBookerHolder) {
      this.aquabasileaCourseBookerHolder = aquabasileaCourseBookerHolder;
      this.userIdToIsFetchingBookedCoursesMap = new HashMap<>();
   }

   public List<Course> getBookedCourses(String userId) {
      if (isUserCurrentlyFetching(userId)) {
         LOG.warn("User [{}] is already fetching the booked courses", userId);
         return List.of();
      }
      try {
         LOG.info("Fetching booked courses for user [{}]", userId);
         userIdToIsFetchingBookedCoursesMap.put(userId, true);
         AquabasileaCourseBooker aquabasileaCourseBooker = aquabasileaCourseBookerHolder.getForUserId(userId);
         return aquabasileaCourseBooker.getBookedCourses();
      } catch (Exception e) {
         LOG.error("Error while fetching booked courses!", e);
      } finally {
         userIdToIsFetchingBookedCoursesMap.remove(userId);
      }
      return List.of();
   }

   private boolean isUserCurrentlyFetching(String userId) {
      return userIdToIsFetchingBookedCoursesMap.containsKey(userId) && userIdToIsFetchingBookedCoursesMap.get(userId);
   }
}
