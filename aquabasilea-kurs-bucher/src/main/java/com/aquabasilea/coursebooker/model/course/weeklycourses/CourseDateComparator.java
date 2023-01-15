package com.aquabasilea.coursebooker.model.course.weeklycourses;

import java.time.LocalDateTime;
import java.util.Comparator;

public class CourseDateComparator implements Comparator<LocalDateTime> {
   @Override
   public int compare(LocalDateTime courseDate1, LocalDateTime courseDate2) {

      // and only if both weekdays are same, compare the entire date
      if (courseDate1.getDayOfWeek() == courseDate2.getDayOfWeek()) {
         // but switch courseDate so the current TUESDAY (e.q. 5.7) comes after the futur TUESDAY (12.7) -> chronological order
         int compareLocalDate = courseDate1.compareTo(courseDate2);
         if (compareLocalDate == 0) {
            return courseDate1.toLocalTime().compareTo(courseDate2.toLocalTime());
         }
         return compareLocalDate;
      }
      // Just compare the week days, in order to get an order like 'monday', 'tuesday'..
      return courseDate1.getDayOfWeek().compareTo(courseDate2.getDayOfWeek());
   }
}
