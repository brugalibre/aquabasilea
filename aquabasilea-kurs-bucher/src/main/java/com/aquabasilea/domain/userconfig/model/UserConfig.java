package com.aquabasilea.domain.userconfig.model;

import com.aquabasilea.domain.course.model.CourseLocation;
import com.brugalibre.common.domain.model.AbstractDomainModel;

import java.util.List;
import java.util.Objects;

/**
 * The {@link UserConfig} contains the users personal configuration
 */
public class UserConfig extends AbstractDomainModel {
   private String id;
   private String userId;
   private List<CourseLocation> courseLocations;

   public UserConfig(String userId, List<CourseLocation> courseLocations) {
      this.userId = userId;
      this.courseLocations = courseLocations;
   }

   @Override
   public String getId() {
      return id;
   }

   @Override
   public void setId(String id) {
      this.id = id;
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

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      UserConfig that = (UserConfig) o;
      return Objects.equals(id, that.id) && Objects.equals(userId, that.userId) && Objects.equals(courseLocations, that.courseLocations);
   }

   @Override
   public int hashCode() {
      return Objects.hash(id, userId, courseLocations);
   }
}
