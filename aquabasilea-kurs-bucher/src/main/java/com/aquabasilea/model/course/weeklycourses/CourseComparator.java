package com.aquabasilea.model.course.weeklycourses;

import java.time.LocalDateTime;
import java.util.Comparator;

public class CourseComparator implements Comparator<Course> {
   @Override
   public int compare(Course course1, Course course2) {
      LocalDateTime courseDate1 = course1.getCourseDate();
      LocalDateTime courseDate2 = course2.getCourseDate();
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
