package com.aquabasilea.model.course.weeklycourses;

import com.aquabasilea.model.AbstractDomainModel;
import com.aquabasilea.model.course.CourseLocation;
import com.aquabasilea.model.course.LocalDateTimeBuilder;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Course extends AbstractDomainModel {
   private String courseName;
   private DayOfWeek dayOfWeek;
   private String timeOfTheDay;
   private CourseLocation courseLocation;

   private boolean isPaused;
   private boolean hasCourseDef;
   private LocalDateTime courseDate;

   public Course() {
      this.isPaused = false;
      this.hasCourseDef = false;
   }

   public String getTimeOfTheDay() {
      return timeOfTheDay;
   }

   public void setTimeOfTheDay(String timeOfTheDay) {
      this.timeOfTheDay = timeOfTheDay;
   }

   public LocalDateTime getCourseDate() {
      if (isNull(courseDate)) {
         this.courseDate = LocalDateTimeBuilder.createLocalDateTime(this.dayOfWeek, this.timeOfTheDay);
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

   public DayOfWeek getDayOfWeek() {
      return dayOfWeek;
   }

   public boolean getIsPaused() {
      return isPaused;
   }

   public void setIsPaused(boolean isPaused) {
      this.isPaused = isPaused;
   }

   public void setDayOfWeek(DayOfWeek dayOfWeek) {
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
      private DayOfWeek dayOfWeek;
      private String courseName;
      private String timeOfTheDay;
      private boolean isPaused;
      private boolean hasCourseDef;
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

      public CourseBuilder withHasCourseDef(boolean hasCourseDef) {
         this.hasCourseDef = hasCourseDef;
         return this;
      }

      public CourseBuilder withDayOfWeek(DayOfWeek dayOfWeek) {
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
         course.hasCourseDef = hasCourseDef;
         return course;
      }

      public static CourseBuilder builder() {
         return new CourseBuilder();
      }
   }

}
