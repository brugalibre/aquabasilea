package com.aquabasilea.model.course.weeklycourses;

import com.aquabasilea.i18n.TextResources;
import com.aquabasilea.model.AbstractDomainModel;
import com.aquabasilea.model.course.coursedef.CourseDef;
import com.aquabasilea.model.course.exception.CourseAlreadyExistsException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

public class WeeklyCourses extends AbstractDomainModel {
   private List<Course> courses;

   public WeeklyCourses() {
      this.courses = new ArrayList<>();
   }

   public WeeklyCourses(List<Course> courses) {
      setCourses(courses);
   }

   public void setCourses(List<Course> courses) {
      // list is null, if there are no entries defined in the yml-file -> no empty list :(
      if (isNull(courses)) {
         courses = new ArrayList<>();
      }
      this.courses = new ArrayList<>(requireNonNull(courses));
   }

   public List<Course> getCourses() {
      return Collections.unmodifiableList(courses);
   }

   /**
    * Adds the given {@link Course}
    *
    * @param course the course to add
    */
   public void addCourse(Course course) {
      if (!existsCourseAlready(course)) {
         courses.add(course);
      } else {
         // exception-handling in web-ui
         throw new CourseAlreadyExistsException(TextResources.ERROR_COURSE_ALREADY_EXISTS.formatted(course.getCourseName()));
      }
   }

   private boolean existsCourseAlready(Course newCourse) {
      return courses.stream()
              .anyMatch(course -> course.getCourseName().equals(newCourse.getCourseName())
                      && course.getDayOfWeek().equals(newCourse.getDayOfWeek())
                      && course.getTimeOfTheDay().equals(newCourse.getTimeOfTheDay())
                      && course.getCourseLocation().equals(newCourse.getCourseLocation()));
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

   /**
    * This Method checks for each {@link Course} if it has an equivalent aquabasilea course aka
    * {@link CourseDef} and updates the attribute {@link Course#getHasCourseDef()}
    *
    * @param courseDefs the new {@link CourseDef} which are extracted from the aquabasilea course page
    */
   public void updateCoursesHasCourseDef(List<CourseDef> courseDefs) {
      getCourses().stream()
              .map(setHasCourseDef(courseDefs))
              .forEach(this::changeCourse);
   }

   private static Function<Course, Course> setHasCourseDef(List<CourseDef> courseDefs) {
      return course -> {
         course.setHasCourseDef(courseDefs.stream()
                 .anyMatch(existCourseDefPredicate(course)));
         if (!course.getHasCourseDef()) {
            course.shiftCourseDateByDays(7);
            course.setHasCourseDef(courseDefs.stream()
                    .anyMatch(existCourseDefPredicate(course)));
         }
         return course;
      };
   }

   private static Predicate<CourseDef> existCourseDefPredicate(Course course) {
      return courseDef -> courseDef.courseName().equals(course.getCourseName())
              && courseDef.courseLocation().equals(course.getCourseLocation())
              && courseDef.courseDate().equals(course.getCourseDate().toLocalDate())
              && courseDef.timeOfTheDay().equals(course.getTimeOfTheDay());
   }
}
