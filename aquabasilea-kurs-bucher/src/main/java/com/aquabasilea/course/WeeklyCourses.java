package com.aquabasilea.course;

import com.aquabasilea.util.YamlUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class WeeklyCourses {
   private List<Course> courses;

   public WeeklyCourses (){
      this.courses = new ArrayList<>();
   }

   public void setCourses(List<Course> courses) {
      this.courses = requireNonNull(courses);
   }

   public List<Course> getCourses() {
      return Collections.unmodifiableList(courses);
   }

   /**
    * For each {@link Course} of this {@link WeeklyCourses} the {@link Course#getCourseDate()} is shifted forwards by
    * the given amount of days
    * @param days the amount of days
    */
   public void shiftCourseDateByDays(int days) {
      this.courses = courses.stream()
              .map(course -> course.shiftCourseDateByDays(days))
              .collect(Collectors.toList());
   }

   /**
    * Loads a {@link WeeklyCourses} form the given yml-File
    *
    * @param ymlFile the file
    * @return a {@link WeeklyCourses}
    */
   public static WeeklyCourses readWeeklyCourses(String ymlFile) {
      return YamlUtil.readYaml(ymlFile, WeeklyCourses.class);
   }
}
