package com.aquabasilea.coursebooker.model.course.weeklycourses;

import com.aquabasilea.coursebooker.model.course.weeklycourses.exception.CourseAlreadyExistsException;
import com.aquabasilea.coursedef.model.CourseDef;
import com.aquabasilea.i18n.TextResources;
import com.brugalibre.common.domain.model.AbstractDomainModel;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

public class WeeklyCourses extends AbstractDomainModel {

   private String userId;

   private List<Course> courses;

   public WeeklyCourses(String userId) {
      this(userId, new ArrayList<>());
   }

   public WeeklyCourses() {
      this.courses = new ArrayList<>();
   }

   public WeeklyCourses(String userId, List<Course> courses) {
      this.userId = userId;
      setCourses(courses);
   }

   public void setCourses(List<Course> courses) {
      // list is null, if there are no entries defined in the yml-file -> no empty list :(
      if (isNull(courses)) {
         courses = new ArrayList<>();
      }
      this.courses = new ArrayList<>(requireNonNull(courses));
   }

   public String getUserId() {
      return userId;
   }

   public void setUserId(String userId) {
      this.userId = userId;
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
                      && hasSameCourseDate(course.getCourseDate(), newCourse)
                      && course.getCourseLocation().equals(newCourse.getCourseLocation()));
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
         // Course has no courseDef -> assume that it's course-date lays in the past. shift a week forward and try again
         if (!course.getHasCourseDef()) {
            course.shiftCourseDateByDays(7);
            course.setHasCourseDef(courseDefs.stream()
                    .anyMatch(existCourseDefPredicate(course)));
            if (!course.getHasCourseDef()) {
               // Still no course-def -> the assumption above was wrong, revert changes to the course-date
               course.shiftCourseDateByDays(-7);
            }
         }
         return course;
      };
   }

   private static Predicate<CourseDef> existCourseDefPredicate(Course course) {
      return courseDef -> courseDef.courseName().equals(course.getCourseName())
              && courseDef.courseLocation().equals(course.getCourseLocation())
              && hasSameCourseDate(courseDef.courseDate(), course);
   }

   /**
    * Verifies if the given course date is the same as the other courses one. Same means, that the time of the day
    * and the day of the week are equal. This means, that the actual date can be a week or two apart and this method
    * still returns true.
    * <p>
    * That's necessary, otherwise we could add the same course twice (once with today as the course-date and a 2nd time
    * with a course date = today + 7 days) and as soo as we shift the first course-date a week into the futur, we have to identical courses.
    */
   private static boolean hasSameCourseDate(LocalDateTime courseDateCourseDef, Course course) {
      LocalTime courseDefTime = courseDateCourseDef.toLocalTime();
      LocalTime courseTime = course.getCourseDate().toLocalTime();
      return courseDateCourseDef.getDayOfWeek() == course.getCourseDate().getDayOfWeek()
              && (courseDefTime.getHour() == courseTime.getHour() && courseDefTime.getMinute() == courseTime.getMinute());
   }
}
