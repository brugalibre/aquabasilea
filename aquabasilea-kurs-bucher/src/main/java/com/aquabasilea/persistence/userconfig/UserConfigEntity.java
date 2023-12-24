package com.aquabasilea.persistence.userconfig;

import com.aquabasilea.persistence.courselocation.CourseLocationEntity;
import com.brugalibre.common.domain.persistence.DomainEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Entity
@Table(name = "user_config")
public class UserConfigEntity extends DomainEntity {

   @NotNull
   @Column(name = "user_id")
   private String userId;

   @NotNull
   @OneToMany(fetch = FetchType.EAGER)
   private List<CourseLocationEntity> courseLocations;

   public UserConfigEntity() {
      super(null);
   }

   public String getUserId() {
      return userId;
   }

   public void setUserId(String userId) {
      this.userId = userId;
   }

   public List<CourseLocationEntity> getCourseLocations() {
      return courseLocations;
   }

   public void setCourseLocations(List<CourseLocationEntity> courseLocations) {
      this.courseLocations = courseLocations;
   }
}
