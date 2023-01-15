package com.aquabasilea.rest.service.admin;

import com.aquabasilea.coursebooker.model.course.weeklycourses.CourseDateComparator;
import com.aquabasilea.rest.model.admin.Course4AdminViewDto;

public class CourseDtoAdminViewComparator implements java.util.Comparator<Course4AdminViewDto> {
   @Override
   public int compare(Course4AdminViewDto course4AdminViewDto1, Course4AdminViewDto course4AdminViewDto2) {
      return new CourseDateComparator().compare(course4AdminViewDto1.courseDate(), course4AdminViewDto2.courseDate());
   }
}
