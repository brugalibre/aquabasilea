package com.aquabasilea.model.course.weeklycourses;

import com.aquabasilea.model.course.LocalDateTimeBuilder;

import java.time.LocalDateTime;
import java.util.Comparator;

public class CourseComparator implements Comparator<Course> {
   @Override
   public int compare(Course course1, Course course2) {
      LocalDateTime courseDate1 = LocalDateTimeBuilder.createCourseDate(course1.getDayOfWeek(), course1.getTimeOfTheDay());
      LocalDateTime courseDate2 = LocalDateTimeBuilder.createCourseDate(course2.getDayOfWeek(), course2.getTimeOfTheDay());
      // and only if both weekdays are same, compare the entire date
      if (courseDate1.getDayOfWeek() == courseDate2.getDayOfWeek()) {
         return courseDate1.compareTo(courseDate2);
      }
      // Just compare the week days, in order to get an order like 'monday', 'tuesday'..
      return courseDate1.getDayOfWeek().compareTo(courseDate2.getDayOfWeek());
   }
}
