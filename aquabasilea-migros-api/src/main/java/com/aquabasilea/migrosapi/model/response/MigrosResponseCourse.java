package com.aquabasilea.migrosapi.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

import static java.util.Objects.nonNull;

/**
 * A {@link MigrosResponseCourse} represents a migros-course when receiving a course via the migros-api
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class MigrosResponseCourse {
   private int centerId;
   private int courseIdTac;
   private String title;
   private String description;
   private String instructor;
   private String location;
   private String start;
   private boolean booked;
   private boolean bookable;

   public MigrosResponseCourse() {
      // empty for jackson
   }

   public int getCenterId() {
      return centerId;
   }

   public void setCenterId(int centerId) {
      this.centerId = centerId;
   }

   public int getCourseIdTac() {
      return courseIdTac;
   }

   public void setCourseIdTac(int courseIdTac) {
      this.courseIdTac = courseIdTac;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public String getInstructor() {
      return instructor;
   }

   public void setInstructor(String instructor) {
      if (nonNull(instructor)) {
         // we need to remove leading white spaces!
         this.instructor = instructor.trim();
      }
   }

   public String getLocation() {
      return location;
   }

   public void setLocation(String location) {
      if (Objects.nonNull(location)) {
         this.location = location.trim();
      }
   }

   public String getStart() {
      return start;
   }

   public void setStart(String start) {
      this.start = start;
   }

   public boolean isBooked() {
      return booked;
   }

   public void setBooked(boolean booked) {
      this.booked = booked;
   }

   public boolean isBookable() {
      return bookable;
   }

   public void setBookable(boolean bookable) {
      this.bookable = bookable;
   }

   @Override
   public String toString() {
      return "MigrosCourse{" +
              "centerId=" + centerId +
              ", courseIdTac=" + courseIdTac +
              ", title='" + title + '\'' +
              ", description='" + description + '\'' +
              ", instructor='" + instructor + '\'' +
              ", location='" + location + '\'' +
              ", start='" + start + '\'' +
              ", booked=" + booked +
              ", bookable=" + bookable +
              '}';
   }
}