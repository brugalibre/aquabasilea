package com.aquabasilea.domain.coursedef.model;

import com.aquabasilea.domain.course.model.CourseLocation;
import com.aquabasilea.domain.course.model.Course;
import com.brugalibre.common.domain.model.DomainModel;

import java.time.LocalDateTime;

/**
 * The {@link CourseDef} defines a bookable {@link Course}.
 * There is only one instance of a {@link CourseDef} but for one {@link CourseDef} there can be many {@link Course}s
 */
public record CourseDef(String id, String userId, LocalDateTime courseDate, CourseLocation courseLocation,
                        String courseName, String courseInstructor) implements DomainModel {
   @Override
   public String getId() {
      return id;
   }

   /**
    * Sets the given user  id as the <code>userId</code> of this {@link CourseDef} and returns a copy of this instance
    *
    * @param userId the id of the user to set
    * @return a copy of this instance with the given userId
    */
   public CourseDef setUserId(String userId) {
      return new CourseDef(this.id, userId, this.courseDate, this.courseLocation, this.courseName, this.courseInstructor);
   }
}
