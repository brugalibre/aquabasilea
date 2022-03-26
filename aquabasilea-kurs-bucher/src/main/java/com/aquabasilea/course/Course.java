package com.aquabasilea.course;

import java.time.LocalDateTime;

import static java.util.Objects.isNull;

public class Course {
   private String courseName;
   private String dayOfWeek;
   private String timeOfTheDay;
   private LocalDateTime courseDate;

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

   public void setDayOfWeek(String dayOfWeek) {
      this.dayOfWeek = dayOfWeek;
   }


   @Override
   public String toString() {
      return "Course{" +
              "courseName='" + courseName + '\'' +
              ", dayOfWeek='" + dayOfWeek + '\'' +
              ", timeOfTheDay='" + timeOfTheDay + '\'' +
              ", courseDate=" + courseDate +
              '}';
   }

   public static class CourseBuilder {
      private String dayOfWeek;
      private String courseName;
      private String timeOfTheDay;

      private CourseBuilder() {
         // private
      }

      public CourseBuilder withCourseName(String courseName) {
         this.courseName = courseName;
         return this;
      }

      public CourseBuilder withTimeOfTheDay(String timeOfTheDay) {
         this.timeOfTheDay = timeOfTheDay;
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
         return course;
      }

      public static CourseBuilder builder() {
         return new CourseBuilder();
      }
   }

}
