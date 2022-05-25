package com.aquabasilea.persistence.entity.course.user;

import com.aquabasilea.course.CourseLocation;
import com.aquabasilea.persistence.entity.base.BaseEntity;
import com.aquabasilea.persistence.entity.course.aquabasilea.CourseDefEntity;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "course")
public class CourseEntity extends BaseEntity {

   @ManyToOne
   @JoinColumn(name = "weeklycourses_id")
   private WeeklyCoursesEntity weeklyCoursesEntity;

   @NonNull
   private String courseName;

   @NonNull
   @Enumerated(EnumType.STRING)
   private DayOfWeek dayOfWeek;

   @NonNull
   private String timeOfTheDay;

   private boolean hasCourseDef;

   @Enumerated(EnumType.STRING)
   @NonNull
   private CourseLocation courseLocation;

   private boolean isPaused;

   public CourseEntity() {
      // private constructor for JPA
      super(null);
      this.isPaused = false;
   }

   public WeeklyCoursesEntity getWeeklyCoursesEntity() {
      return weeklyCoursesEntity;
   }

   public void setWeeklyCoursesEntity(WeeklyCoursesEntity weeklyCoursesEntity) {
      this.weeklyCoursesEntity = weeklyCoursesEntity;
   }

   @NonNull
   public CourseLocation getCourseLocation() {
      return courseLocation;
   }

   public void setCourseLocation(@NonNull CourseLocation courseLocation) {
      this.courseLocation = courseLocation;
   }

   @NonNull
   public String getCourseName() {
      return courseName;
   }

   public void setCourseName(@NonNull String courseName) {
      this.courseName = courseName;
   }

   @NonNull
   public DayOfWeek getDayOfWeek() {
      return dayOfWeek;
   }

   public void setDayOfWeek(@NonNull DayOfWeek dayOfWeek) {
      this.dayOfWeek = dayOfWeek;
   }

   @NonNull
   public String getTimeOfTheDay() {
      return timeOfTheDay;
   }

   public void setTimeOfTheDay(@NonNull String timeOfTheDay) {
      this.timeOfTheDay = timeOfTheDay;
   }

   public boolean getIsPaused() {
      return isPaused;
   }

   public void setIsPaused(boolean isPaused) {
      this.isPaused = isPaused;
   }

   public boolean getHasCourseDef() {
      return hasCourseDef;
   }

   public void setHasCourseDef(boolean hasCourseDef) {
      this.hasCourseDef = hasCourseDef;
   }

   @Override
   public String toString() {
      return "CourseEntity{" +
              "weeklyCoursesEntity=" + weeklyCoursesEntity +
              ", courseName='" + courseName + '\'' +
              ", dayOfWeek='" + dayOfWeek + '\'' +
              ", timeOfTheDay='" + timeOfTheDay + '\'' +
              ", hasCourseDef=" + hasCourseDef +
              ", courseLocation=" + courseLocation +
              ", isPaused=" + isPaused +
              '}';
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;
      CourseEntity that = (CourseEntity) o;
      return hasCourseDef == that.hasCourseDef && isPaused == that.isPaused && Objects.equals(weeklyCoursesEntity, that.weeklyCoursesEntity) && courseName.equals(that.courseName) && dayOfWeek.equals(that.dayOfWeek) && timeOfTheDay.equals(that.timeOfTheDay) && courseLocation == that.courseLocation;
   }

   @Override
   public int hashCode() {
      return Objects.hash(super.hashCode(), weeklyCoursesEntity, courseName, dayOfWeek, timeOfTheDay, hasCourseDef, courseLocation, isPaused);
   }
}
