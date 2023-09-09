package com.aquabasilea.persistence.userconfig;

import com.aquabasilea.domain.course.model.CourseLocation;
import com.brugalibre.common.domain.persistence.DomainEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.util.List;

@Entity
@Table(name = "user_config")
public class UserConfigEntity extends DomainEntity {

   @NotNull
   @Column(name = "user_id")
   private String userId;

   @ElementCollection
   @Enumerated(value = EnumType.STRING)
   @LazyCollection(LazyCollectionOption.FALSE)
   private List<CourseLocation> courseLocations;

   public UserConfigEntity() {
      super(null);
   }

   public String getUserId() {
      return userId;
   }

   public void setUserId(String userId) {
      this.userId = userId;
   }

   public List<CourseLocation> getCourseLocations() {
      return courseLocations;
   }

   public void setCourseLocations(List<CourseLocation> courseLocations) {
      this.courseLocations = courseLocations;
   }
}
