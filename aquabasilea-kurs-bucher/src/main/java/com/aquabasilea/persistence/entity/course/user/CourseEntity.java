package com.aquabasilea.persistence.entity.course.user;

import com.aquabasilea.course.CourseLocation;
import com.aquabasilea.persistence.entity.base.BaseEntity;
import com.aquabasilea.persistence.entity.course.aquabasilea.CourseDefEntity;
import org.springframework.lang.NonNull;

import javax.persistence.*;
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
   private String dayOfWeek;

   @NonNull
   private String timeOfTheDay;

   @Enumerated(EnumType.STRING)
   @NonNull
   private CourseLocation courseLocation;

   private boolean isPaused;

   public CourseEntity() {
      // private constructor for JPA
      super(null);
      this.isPaused = false;
   }

   public CourseEntity(WeeklyCoursesEntity weeklyCoursesEntity, @NonNull UUID id, @NonNull String courseName,
                       @NonNull String dayOfWeek, @NonNull String timeOfTheDay,
                       @NonNull CourseLocation courseLocation, boolean isPaused) {
      super(id);
      this.weeklyCoursesEntity = weeklyCoursesEntity;
      this.courseName = courseName;
      this.dayOfWeek = dayOfWeek;
      this.timeOfTheDay = timeOfTheDay;
      this.courseLocation = courseLocation;
      this.isPaused = isPaused;
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
   public String getDayOfWeek() {
      return dayOfWeek;
   }

   public void setDayOfWeek(@NonNull String dayOfWeek) {
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

   @Override
   public String toString() {
      return "CourseEntity{" +
              "weeklyCoursesEntity=" + weeklyCoursesEntity +
              ", id='" + id + '\'' +
              ", courseName='" + courseName + '\'' +
              ", dayOfWeek='" + dayOfWeek + '\'' +
              ", timeOfTheDay='" + timeOfTheDay + '\'' +
              ", isPaused=" + isPaused +
              '}';
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;
      CourseEntity that = (CourseEntity) o;
      return isPaused == that.isPaused && Objects.equals(weeklyCoursesEntity, that.weeklyCoursesEntity) && courseName.equals(that.courseName) && dayOfWeek.equals(that.dayOfWeek) && timeOfTheDay.equals(that.timeOfTheDay) && courseLocation == that.courseLocation;
   }

   @Override
   public int hashCode() {
      return Objects.hash(super.hashCode(), weeklyCoursesEntity, courseName, dayOfWeek, timeOfTheDay, courseLocation, isPaused);
   }
}
