package com.aquabasilea.persistence.coursedef;

import com.aquabasilea.persistence.courselocation.CourseLocationEntity;
import com.brugalibre.common.domain.persistence.DomainEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "coursedef")
public class CourseDefEntity extends DomainEntity {

   @NotNull
   @Column(name = "user_id")
   private String userId;

   @NotNull
   private String courseName;

   @NotNull
   private String courseInstructor;

   @NotNull
   private LocalDateTime courseDate;

   @NotNull
   @OneToOne(fetch = FetchType.EAGER)
   @JoinColumn(name = "courselocation_id")
   private CourseLocationEntity courseLocation;

   @NotNull
   public String getUserId() {
      return userId;
   }

   public void setUserId(@NotNull String userId) {
      this.userId = userId;
   }

   public CourseDefEntity() {
      // private constructor for JPA
      super(null);
   }

   @Override
   public String toString() {
      return "CourseDefEntity{" +
              "courseName='" + courseName + '\'' +
              "courseInstructor='" + courseInstructor + '\'' +
              ", courseDate='" + courseDate + '\'' +
              ", courseLocation=" + courseLocation +
              '}';
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;
      CourseDefEntity that = (CourseDefEntity) o;
      return Objects.equals(userId, that.userId) && Objects.equals(courseName, that.courseName) && Objects.equals(courseInstructor, that.courseInstructor) && Objects.equals(courseDate, that.courseDate) && courseLocation == that.courseLocation;
   }

   @Override
   public int hashCode() {
      return Objects.hash(super.hashCode(), userId, courseName, courseInstructor, courseDate, courseLocation);
   }

   @NotNull
   public String getCourseName() {
      return courseName;
   }

   public void setCourseName(@NotNull String courseName) {
      this.courseName = courseName;
   }

   @NotNull
   public LocalDateTime getCourseDate() {
      return courseDate;
   }

   public void setCourseDate(@NotNull LocalDateTime courseDate) {
      this.courseDate = courseDate;
   }

   @NotNull
   public CourseLocationEntity getCourseLocation() {
      return courseLocation;
   }

   public void setCourseLocation(CourseLocationEntity courseLocationEntity) {
      this.courseLocation = courseLocationEntity;
   }

   public String getCourseInstructor() {
      return courseInstructor;
   }

   public void setCourseInstructor(String courseInstructor) {
      this.courseInstructor = courseInstructor;
   }
}
