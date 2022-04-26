package com.aquabasilea.course;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

public class WeeklyCourses {
   private List<Course> courses;

   public WeeklyCourses() {
      this.courses = new ArrayList<>();
   }

   public WeeklyCourses(List<Course> courses) {
      setCourses(courses);
   }

   public void setCourses(List<Course> courses) {
      // list is null, if there are no entries defined in the yml-file -> no empty list :(
      if (isNull(courses)){
         courses = new ArrayList<>();
      }
      this.courses = new ArrayList<>(requireNonNull(courses));
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
    * Adds the given {@link Course}
    *
    * @param course the course to add
    */
   public void addCourse(Course course) {
      courses.add(course);
   }

   /**
    * Removes the {@link Course} for the given id
    *
    * @param courseId the id of the course to remove
    */
   public void removeCourseById(String courseId) {
      Course course4Id = getCourse4Id(courseId);
      courses.remove(course4Id);
   }

   /**
    * Takes the changes from the given {@link Course} and applies them
    * to a Course of this {@link WeeklyCourses} with the same id
    *
    * @param changedCourse the changedCourse to remove
    */
   public void changeCourse(Course changedCourse) {
      Course course2Change = getCourse4Id(changedCourse.getId());
      changeFoundCourse(course2Change, changedCourse);
   }

   /**
    * Pauses or resumes the {@link Course} for the given id
    *
    * @param courseId the id of the {@link Course} to suspend / resume
    */
   public void pauseResumeCourse(String courseId) {
      Course course2Change = getCourse4Id(courseId);
      course2Change.setIsPaused(!course2Change.getIsPaused());
      changeFoundCourse(course2Change, course2Change);
   }

   /**
    * Finds and returns a {@link Course} instance for the given id or <code>null</code> if there is
    * no such element
    *
    * @param courseId the id of the {@link Course} to find
    * @return a {@link Course} instance for the given id or <code>null</code> if there is
    */
   private Course getCourse4Id(String courseId) {
      return courses.stream()
              .filter(course -> course.getId().equals(courseId))
              .findFirst()
              .orElse(null);
   }

   private static void changeFoundCourse(Course course2Change, Course changedCourse) {
      course2Change.setCourseName(changedCourse.getCourseName());
      course2Change.setTimeOfTheDay(changedCourse.getTimeOfTheDay());
      course2Change.setDayOfWeek(changedCourse.getDayOfWeek());
      course2Change.setIsPaused(changedCourse.getIsPaused());
   }
}
