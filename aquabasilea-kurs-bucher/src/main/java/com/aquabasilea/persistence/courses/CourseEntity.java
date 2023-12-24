package com.aquabasilea.persistence.courses;

import com.aquabasilea.persistence.courselocation.CourseLocationEntity;
import com.brugalibre.common.domain.persistence.DomainEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "course")
public class CourseEntity extends DomainEntity {

   @ManyToOne
   @JoinColumn(name = "weeklycourses_id")
   private WeeklyCoursesEntity weeklyCoursesEntity;

   @NotNull
   private String courseName;

   @NotNull
   private String courseInstructor;

   @NotNull
   private LocalDateTime courseDate;

   private boolean hasCourseDef;

   @NotNull
   @OneToOne(fetch = FetchType.EAGER)
   @JoinColumn(name = "courselocation_id")
   private CourseLocationEntity courseLocation;

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

   @NotNull
   public LocalDateTime getCourseDate() {
      return courseDate;
   }

   public void setCourseDate(@NotNull LocalDateTime courseDate) {
      this.courseDate = courseDate;
   }

   @NotNull
   public String getCourseName() {
      return courseName;
   }

   public void setCourseName(@NotNull String courseName) {
      this.courseName = courseName;
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

   public String getCourseInstructor() {
      return courseInstructor;
   }

   public void setCourseInstructor(String courseInstructor) {
      this.courseInstructor = courseInstructor;
   }

   public boolean isHasCourseDef() {
      return hasCourseDef;
   }

   public CourseLocationEntity getCourseLocation() {
      return courseLocation;
   }

   public void setCourseLocation(CourseLocationEntity courseLocation) {
      this.courseLocation = courseLocation;
   }

   public boolean isPaused() {
      return isPaused;
   }

   public void setPaused(boolean paused) {
      isPaused = paused;
   }

   @Override
   public String toString() {
      return "CourseEntity{" +
              "weeklyCoursesEntity=" + weeklyCoursesEntity.getId() +
              ", courseName='" + courseName + '\'' +
              ", courseInstructor='" + courseInstructor + '\'' +
              ", courseDate='" + courseDate + '\'' +
              ", hasCourseDef=" + hasCourseDef +
              ", courseLocationEntity=" + courseLocation +
              ", isPaused=" + isPaused +
              '}';
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;
      CourseEntity that = (CourseEntity) o;
      return hasCourseDef == that.hasCourseDef && isPaused == that.isPaused &&
              Objects.equals(weeklyCoursesEntity, that.weeklyCoursesEntity) && Objects.equals(courseName, that.courseName)
              && courseInstructor.equals(that.courseInstructor)
              && Objects.equals(courseDate, that.courseDate) && courseLocation == that.courseLocation;
   }

   @Override
   public int hashCode() {
      return Objects.hash(super.hashCode(), weeklyCoursesEntity, courseName, courseInstructor, courseDate, hasCourseDef, courseLocation, isPaused);
   }
}
