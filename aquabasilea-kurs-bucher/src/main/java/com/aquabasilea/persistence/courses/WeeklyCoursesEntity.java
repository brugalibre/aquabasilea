package com.aquabasilea.persistence.courses;

import com.brugalibre.common.domain.persistence.DomainEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

@Entity
@Table(name = "weeklycourses")
public class WeeklyCoursesEntity extends DomainEntity {

   @NotNull
   @Column(name = "user_id")
   private String userId;

   /*
    * yes eager, since we access them in the mapper, where the session is already closed.
    * Besides, this shouldn't be a performance issue, since there are not that many courses.
    */
   @OneToMany(targetEntity = CourseEntity.class,
           mappedBy = "weeklyCoursesEntity",
           cascade = CascadeType.ALL,
           fetch = FetchType.EAGER,
           orphanRemoval = true)
   @NotNull
   private List<CourseEntity> courses;

   public WeeklyCoursesEntity() {
      this(null);
   }

   /**
    * Creates a new {@link WeeklyCoursesEntity}
    *
    * @param id the id
    */
   public WeeklyCoursesEntity(String id) {
      super(id);
      this.courses = new ArrayList<>();
   }

   @NotNull
   public List<CourseEntity> getCourses() {
      return courses;
   }

   public void setCourses(List<CourseEntity> coursesEntities) {
      this.courses = requireNonNull(coursesEntities);
   }

   @NotNull
   public String getUserId() {
      return userId;
   }

   public void setUserId(@NotNull String userId) {
      this.userId = userId;
   }

   @Override
   public String toString() {
      return "WeeklyCoursesEntity{" +
              "coursesEntities=" + courses +
              ", id=" + id +
              '}';
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;
      WeeklyCoursesEntity that = (WeeklyCoursesEntity) o;
      return courses.equals(that.courses);
   }

   @Override
   public int hashCode() {
      return Objects.hash(super.hashCode(), courses);
   }
}
