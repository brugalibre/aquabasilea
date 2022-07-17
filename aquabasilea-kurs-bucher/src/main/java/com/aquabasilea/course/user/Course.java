package com.aquabasilea.course.user;

import com.aquabasilea.course.CourseLocation;
import com.aquabasilea.course.LocalDateTimeBuilder;

import java.time.LocalDateTime;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Course {
   private String id;
   private String courseName;
   private String dayOfWeek;
   private String timeOfTheDay;
   private CourseLocation courseLocation;

   private boolean isPaused;
   private LocalDateTime courseDate;

   public Course() {
      this.isPaused = false;
   }

   public String getTimeOfTheDay() {
      return timeOfTheDay;
   }

   public void setTimeOfTheDay(String timeOfTheDay) {
      this.timeOfTheDay = timeOfTheDay;
   }

   public LocalDateTime getCourseDate() {
      if (isNull(courseDate)) {
         this.courseDate = LocalDateTimeBuilder.createCourseDate(this.dayOfWeek, this.timeOfTheDay);
      }
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

   public String getDayOfWeek() {
      return dayOfWeek;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      if (nonNull(id)) {
         this.id = id;
      }
   }

   public boolean getIsPaused() {
      return isPaused;
   }

   public void setIsPaused(boolean isPaused) {
      this.isPaused = isPaused;
   }

   public void setDayOfWeek(String dayOfWeek) {
      this.dayOfWeek = dayOfWeek;
   }

   public CourseLocation getCourseLocation() {
      return courseLocation;
   }

   public void setCourseLocation(CourseLocation courseLocation) {
      this.courseLocation = courseLocation;
   }

   @Override
   public String toString() {
      return "Course{" +
              "id='" + id + '\'' +
              ", courseName='" + courseName + '\'' +
              ", dayOfWeek='" + dayOfWeek + '\'' +
              ", timeOfTheDay='" + timeOfTheDay + '\'' +
              ", courseLocation=" + courseLocation +
              ", isPaused=" + isPaused +
              ", courseDate=" + courseDate +
              '}';
   }

   public static class CourseBuilder {
      private String dayOfWeek;
      private String courseName;
      private String timeOfTheDay;
      private boolean isPaused;
      private CourseLocation courseLocation;
      private String id;

      private CourseBuilder() {
         this.courseLocation = CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA; // default
      }

      public CourseBuilder withCourseLocation(CourseLocation courseLocation) {
         this.courseLocation = courseLocation;
         return this;
      }

      public CourseBuilder withCourseName(String courseName) {
         this.courseName = courseName;
         return this;
      }

      public CourseBuilder withTimeOfTheDay(String timeOfTheDay) {
         this.timeOfTheDay = timeOfTheDay;
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

      public CourseBuilder withDayOfWeek(String dayOfWeek) {
         this.dayOfWeek = dayOfWeek;
         return this;
      }

      public Course build() {
         Course course = new Course();
         course.setCourseName(courseName);
         course.setTimeOfTheDay(timeOfTheDay);
         course.setDayOfWeek(dayOfWeek);
         course.setId(id);
         course.setIsPaused(isPaused);
         course.courseLocation = courseLocation;
         return course;
      }

      public static CourseBuilder builder() {
         return new CourseBuilder();
      }
   }

}
