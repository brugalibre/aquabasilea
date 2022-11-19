package com.aquabasilea.persistence.entity.course.weeklycourses;

import com.aquabasilea.model.course.CourseLocation;
import com.aquabasilea.persistence.entity.base.BaseEntity;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "course")
public class CourseEntity extends BaseEntity {

   @ManyToOne
   @JoinColumn(name = "weeklycourses_id")
   private WeeklyCoursesEntity weeklyCoursesEntity;

   @NonNull
   private String courseName;

   @NonNull
   private String courseInstructor;

   @NonNull
   private LocalDateTime courseDate;

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
   public LocalDateTime getCourseDate() {
      return courseDate;
   }

   public void setCourseDate(@NonNull LocalDateTime courseDate) {
      this.courseDate = courseDate;
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

   @NonNull
   public String getCourseInstructor() {
      return courseInstructor;
   }

   public void setCourseInstructor(String courseInstructor) {
      this.courseInstructor = courseInstructor;
   }

   @Override
   public String toString() {
      return "CourseEntity{" +
              "weeklyCoursesEntity=" + weeklyCoursesEntity +
              ", courseName='" + courseName + '\'' +
              ", courseInstructor='" + courseInstructor + '\'' +
              ", courseDate='" + courseDate + '\'' +
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
      return hasCourseDef == that.hasCourseDef && isPaused == that.isPaused && Objects.equals(weeklyCoursesEntity, that.weeklyCoursesEntity) && Objects.equals(courseName, that.courseName) && courseInstructor.equals(that.courseInstructor) && Objects.equals(courseDate, that.courseDate) && courseLocation == that.courseLocation;
   }

   @Override
   public int hashCode() {
      return Objects.hash(super.hashCode(), weeklyCoursesEntity, courseName, courseInstructor, courseDate, hasCourseDef, courseLocation, isPaused);
   }
}
