package com.aquabasilea.persistence.entity.course.aquabasilea;

import com.aquabasilea.model.course.CourseLocation;
import com.aquabasilea.persistence.entity.base.BaseEntity;
import org.springframework.lang.NonNull;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "coursedef")
public class CourseDefEntity extends BaseEntity {

   @NonNull
   private String courseName;

   @NonNull
   private LocalDate courseDate;

   @NonNull
   private String timeOfTheDay;

   @Enumerated(EnumType.STRING)
   @NonNull
   private CourseLocation courseLocation;

   public CourseDefEntity() {
      // private constructor for JPA
      super(null);
   }

   @Override
   public String toString() {
      return "CourseDefEntity{" +
              "courseName='" + courseName + '\'' +
              ", courseDate='" + courseDate + '\'' +
              ", timeOfTheDay='" + timeOfTheDay + '\'' +
              ", courseLocation=" + courseLocation +
              '}';
   }

   @NonNull
   public String getCourseName() {
      return courseName;
   }

   public void setCourseName(@NonNull String courseName) {
      this.courseName = courseName;
   }

   @NonNull
   public LocalDate getCourseDate() {
      return courseDate;
   }

   public void setCourseDate(@NonNull LocalDate courseDate) {
      this.courseDate = courseDate;
   }

   @NonNull
   public String getTimeOfTheDay() {
      return timeOfTheDay;
   }

   public void setTimeOfTheDay(@NonNull String timeOfTheDay) {
      this.timeOfTheDay = timeOfTheDay;
   }

   @NonNull
   public CourseLocation getCourseLocation() {
      return courseLocation;
   }

   public void setCourseLocation(@NonNull CourseLocation courseLocation) {
      this.courseLocation = courseLocation;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;
      CourseDefEntity that = (CourseDefEntity) o;
      return courseName.equals(that.courseName) && courseDate.equals(that.courseDate) && timeOfTheDay.equals(that.timeOfTheDay) && courseLocation == that.courseLocation;
   }

   @Override
   public int hashCode() {
      return Objects.hash(super.hashCode(), courseName, courseDate, timeOfTheDay, courseLocation);
   }

}
