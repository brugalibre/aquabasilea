package com.aquabasilea.migrosapi.model.getcourse.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.Objects.nonNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MigrosGetCoursesResponse {
   private List<MigrosResponseCourse> courses;

   public MigrosGetCoursesResponse() {
      // default constructor for jackson
      this.courses = new ArrayList<>();
   }

   public List<MigrosResponseCourse> getCourses() {
      return courses;
   }

   public void setCourses(List<MigrosResponseCourse> courses) {
      if (nonNull(courses)) {
         this.courses = courses;
      }
   }

   /**
    * @return the single matched course or <code>null</code> if there is more than one
    */
   public String getSingleCourseIdTac() {
      return courses.stream()
              .sorted(Comparator.comparing(MigrosResponseCourse::getStartAsLocalDateTime))
              .map(MigrosResponseCourse::getCourseIdTac)
              .findFirst()
              .map(String::valueOf)
              .orElse(null);
   }

   public int getResultCount() {
      return courses.size();
   }
}
