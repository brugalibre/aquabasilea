package com.aquabasilea.rest.model.course;

import com.aquabasilea.course.Course;
import com.aquabasilea.course.Course.CourseBuilder;

import java.util.UUID;

import static java.util.Objects.isNull;

public class CourseDto {
   private String id;
   private String courseName;
   private String dayOfWeek;
   private String timeOfTheDay;
   private boolean isPaused;
   private boolean isCurrentCourse;

   private boolean isCourseNameEditable;
   private boolean isDayOfWeekEditable;
   private boolean isTimeOfTheDayEditable;

   public CourseDto(String id, String courseName, String dayOfWeek, String timeOfTheDay,
                    boolean isPaused, boolean isCurrentCourse) {
      this.id = id;
      this.courseName = courseName;
      this.dayOfWeek = dayOfWeek;
      this.timeOfTheDay = timeOfTheDay;
      this.isPaused = isPaused;
      this.isCurrentCourse = isCurrentCourse;
   }

   @Override
   public String toString() {
      return "CourseDto{" +
              "id='" + id + '\'' +
              ", courseName='" + courseName + '\'' +
              ", dayOfWeek='" + dayOfWeek + '\'' +
              ", timeOfTheDay='" + timeOfTheDay + '\'' +
              ", isPaused='" + isPaused + '\'' +
              ", isCurrentCourse='" + isCurrentCourse + '\'' +
              '}';
   }

   public static Course map2Course(CourseDto courseDto) {
      String currentId = isNull(courseDto.id) ? UUID.randomUUID().toString() : courseDto.id;
      return CourseBuilder.builder()
              .withId(currentId)
              .withCourseName(courseDto.getCourseName())
              .withDayOfWeek(courseDto.getDayOfWeek())
              .withTimeOfTheDay(courseDto.getTimeOfTheDay())
              .withIsPaused(courseDto.getIsPaused())
              .build();
   }

   public static CourseDto of(Course course, boolean isCurrentCourse) {
      return new CourseDto(course.getId(), course.getCourseName(), course.getDayOfWeek(),
              course.getTimeOfTheDay(), course.getIsPaused(), isCurrentCourse);
   }

   public String getId() {
      return id;
   }

   public String getCourseName() {
      return courseName;
   }

   public String getDayOfWeek() {
      return dayOfWeek;
   }

   public String getTimeOfTheDay() {
      return timeOfTheDay;
   }

   public boolean getIsPaused() {
      return isPaused;
   }

   public boolean getIsCurrentCourse() {
      return isCurrentCourse;
   }

   public void setIsPaused(boolean isPaused) {
      this.isPaused = isPaused;
   }
   public void setIsCurrentCourse(boolean isCurrentCourse) {
      this.isCurrentCourse = isCurrentCourse;
   }

   public void setId(String id) {
      this.id = id;
   }

   public void setCourseName(String courseName) {
      this.courseName = courseName;
   }

   public void setDayOfWeek(String dayOfWeek) {
      this.dayOfWeek = dayOfWeek;
   }

   public void setTimeOfTheDay(String timeOfTheDay) {
      this.timeOfTheDay = timeOfTheDay;
   }

   public boolean isCourseNameEditable() {
      return isCourseNameEditable;
   }

   public void setCourseNameEditable(boolean courseNameEditable) {
      isCourseNameEditable = courseNameEditable;
   }

   public boolean isDayOfWeekEditable() {
      return isDayOfWeekEditable;
   }

   public void setDayOfWeekEditable(boolean dayOfWeekEditable) {
      isDayOfWeekEditable = dayOfWeekEditable;
   }

   public boolean isTimeOfTheDayEditable() {
      return isTimeOfTheDayEditable;
   }

   public void setTimeOfTheDayEditable(boolean timeOfTheDayEditable) {
      isTimeOfTheDayEditable = timeOfTheDayEditable;
   }
}


