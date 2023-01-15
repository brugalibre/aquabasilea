package com.aquabasilea.coursebooker.model.course.weeklycourses;

import java.util.Comparator;

public class CourseComparator implements Comparator<Course> {
   @Override
   public int compare(Course course1, Course course2) {
      return new CourseDateComparator().compare(course1.getCourseDate(), course2.getCourseDate());
   }
}
