package com.aquabasilea.domain.course.model;

import com.aquabasilea.domain.courselocation.model.CourseLocation;
import com.brugalibre.common.domain.model.AbstractDomainModel;

import java.time.LocalDateTime;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

public class Course extends AbstractDomainModel {
   private String courseName;
   private String courseInstructor;
   private LocalDateTime courseDate;
   private CourseLocation courseLocation;
   private String bookingIdTac;

   private boolean isPaused;
   private boolean hasCourseDef;

   public Course() {
      this.isPaused = false;
      this.hasCourseDef = false;
   }

   public LocalDateTime getCourseDate() {
      return courseDate;
   }

   public Course shiftCourseDateByDays(int days) {
      if (nonNull(this.courseDate)) {
         this.courseDate = this.courseDate.plusDays(days);
      }
      return this;
   }

   public String getCourseName() {
      return courseName;
   }

   public void setCourseName(String courseName) {
      this.courseName = courseName;
   }

   public void setCourseDate(LocalDateTime courseDate) {
      this.courseDate = courseDate;
   }

   public boolean getIsPaused() {
      return isPaused;
   }

   public void setIsPaused(boolean isPaused) {
      this.isPaused = isPaused;
   }

   public CourseLocation getCourseLocation() {
      return courseLocation;
   }

   public void setCourseLocation(CourseLocation courseLocation) {
      this.courseLocation = courseLocation;
   }

   public String getCourseInstructor() {
      return courseInstructor;
   }

   public void setCourseInstructor(String courseInstructor) {
      this.courseInstructor = courseInstructor;
   }

   public String getBookingIdTac() {
      return bookingIdTac;
   }

   public void setBookingIdTac(String bookingIdTac) {
      this.bookingIdTac = bookingIdTac;
   }

   @Override
   public String toString() {
      return "Course{" +
              "id='" + id + '\'' +
              ", courseName='" + courseName + '\'' +
              ", courseInstructor='" + courseInstructor + '\'' +
              ", courseDate='" + courseDate + '\'' +
              ", courseLocation=" + courseLocation +
              ", isPaused=" + isPaused +
              ", hasCourseDef=" + hasCourseDef +
              ", courseDate=" + courseDate +
              '}';
   }

   public boolean getHasCourseDef() {
      return hasCourseDef;
   }

   public void setHasCourseDef(boolean hasCourseDef) {
      this.hasCourseDef = hasCourseDef;
   }

   public static class CourseBuilder {
      private LocalDateTime courseDate;
      private String courseName;
      private String courseInstructor;
      private boolean isPaused;
      private boolean hasCourseDef;
      private CourseLocation courseLocation;
      private String id;

      private CourseBuilder() {
      }

      public CourseBuilder withCourseLocation(CourseLocation courseLocation) {
         this.courseLocation = courseLocation;
         return this;
      }

      public CourseBuilder withCourseName(String courseName) {
         this.courseName = courseName;
         return this;
      }

      public CourseBuilder withCourseInstructor(String courseInstructor) {
         this.courseInstructor = courseInstructor;
         return this;
      }

      public CourseBuilder withId(String id) {
         this.id = id;
         return this;
      }

      public CourseBuilder withIsPaused(boolean isPaused) {
         this.isPaused = isPaused;
         return this;
      }

      public CourseBuilder withHasCourseDef(boolean hasCourseDef) {
         this.hasCourseDef = hasCourseDef;
         return this;
      }

      public CourseBuilder withCourseDate(LocalDateTime courseDate) {
         this.courseDate = courseDate;
         return this;
      }

      public Course build() {
         Course course = new Course();
         course.setCourseName(courseName);
         course.courseInstructor = courseInstructor;
         course.courseDate = courseDate;
         course.setId(id);
         course.setIsPaused(isPaused);
         course.courseLocation = courseLocation;
         course.hasCourseDef = hasCourseDef;
         requireNonNull(courseInstructor, "courseInstructor must not be null");
         requireNonNull(courseDate, "courseDate must not be null");
         requireNonNull(courseLocation, "courseLocation must not be null");
         requireNonNull(courseName, "courseName must not be null");
         return course;
      }

      public static CourseBuilder builder() {
         return new CourseBuilder();
      }
   }

}
