package com.aquabasilea.persistence.entity.course;

import com.aquabasilea.persistence.entity.base.BaseEntity;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@Entity
@Table(name = "weeklycourses")
public class WeeklyCoursesEntity extends BaseEntity {

   /*
    * yes eager, since we access them in the mapper, where the session is already closed.
    * Besides, this shouldn't be a performance issue, since there are not that many courses.
    */
   @OneToMany(targetEntity = CourseEntity.class,
           mappedBy = "weeklyCoursesEntity",
           cascade = CascadeType.ALL,
           fetch = FetchType.EAGER,
           orphanRemoval = true)
   @NonNull
   private List<CourseEntity> coursesEntities;

   public WeeklyCoursesEntity() {
      this(null);
   }

   /**
    * Creates a new {@link WeeklyCoursesEntity}
    *
    * @param id the id
    */
   public WeeklyCoursesEntity(UUID id) {
      super(id);
      this.coursesEntities = new ArrayList<>();
   }

   @NonNull
   public List<CourseEntity> getCoursesEntities() {
      return coursesEntities;
   }

   public void setCoursesEntities(List<CourseEntity> coursesEntities) {
      this.coursesEntities = requireNonNull(coursesEntities);
   }

   @Override
   public String toString() {
      return "WeeklyCoursesEntity{" +
              "coursesEntities=" + coursesEntities +
              ", id=" + id +
              '}';
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;
      WeeklyCoursesEntity that = (WeeklyCoursesEntity) o;
      return coursesEntities.equals(that.coursesEntities);
   }

   @Override
   public int hashCode() {
      return Objects.hash(super.hashCode(), coursesEntities);
   }
}
