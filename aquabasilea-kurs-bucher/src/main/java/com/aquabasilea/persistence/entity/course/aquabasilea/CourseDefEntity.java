package com.aquabasilea.persistence.entity.course.aquabasilea;

import com.aquabasilea.course.CourseLocation;
import com.aquabasilea.persistence.entity.base.BaseEntity;
import org.springframework.lang.NonNull;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.time.DayOfWeek;
import java.util.Objects;

@Entity
@Table(name = "coursedef")
public class CourseDefEntity extends BaseEntity {

   @NonNull
   private String courseName;

   @Enumerated(EnumType.STRING)
   @NonNull
   private DayOfWeek dayOfWeek;

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
              ", dayOfWeek='" + dayOfWeek + '\'' +
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
      return courseName.equals(that.courseName) && dayOfWeek.equals(that.dayOfWeek) && timeOfTheDay.equals(that.timeOfTheDay) && courseLocation == that.courseLocation;
   }

   @Override
   public int hashCode() {
      return Objects.hash(super.hashCode(), courseName, dayOfWeek, timeOfTheDay, courseLocation);
   }

}
