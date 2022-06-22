package com.aquabasilea.persistence.entity.course;

import com.aquabasilea.persistence.entity.base.BaseEntity;
import org.springframework.lang.NonNull;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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

   private boolean isPaused;

   public CourseEntity() {
      // private constructor for JPA
      super(null);
      this.isPaused = false;
   }

   public CourseEntity(WeeklyCoursesEntity weeklyCoursesEntity, @NonNull String description,
                       @NonNull UUID id, @NonNull String courseName, @NonNull String dayOfWeek,
                       @NonNull String timeOfTheDay, boolean isPaused) {
      super(id);
      this.weeklyCoursesEntity = weeklyCoursesEntity;
      this.courseName = courseName;
      this.dayOfWeek = dayOfWeek;
      this.timeOfTheDay = timeOfTheDay;
      this.isPaused = isPaused;
   }

   public WeeklyCoursesEntity getWeeklyCoursesEntity() {
      return weeklyCoursesEntity;
   }

   public void setWeeklyCoursesEntity(WeeklyCoursesEntity weeklyCoursesEntity) {
      this.weeklyCoursesEntity = weeklyCoursesEntity;
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
      return isPaused == that.isPaused && Objects.equals(weeklyCoursesEntity, that.weeklyCoursesEntity) && id.equals(that.id) && courseName.equals(that.courseName) && dayOfWeek.equals(that.dayOfWeek) && timeOfTheDay.equals(that.timeOfTheDay);
   }

   @Override
   public int hashCode() {
      return Objects.hash(super.hashCode(), weeklyCoursesEntity, id, courseName, dayOfWeek, timeOfTheDay, isPaused);
   }
}
