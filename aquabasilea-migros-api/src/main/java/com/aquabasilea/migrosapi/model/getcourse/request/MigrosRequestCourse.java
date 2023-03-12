package com.aquabasilea.migrosapi.model.getcourse.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A {@link MigrosRequestCourse} represents a migros-course sending via the migros-api
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MigrosRequestCourse {
   private int centerId;
   private String coursetitle;

   public MigrosRequestCourse() {
      // empty for jackson
   }

   public MigrosRequestCourse(String centerId, String courseName) {
      this.centerId = Integer.parseInt(centerId);
      this.coursetitle = courseName;
   }

   public int getCenterId() {
      return centerId;
   }

   public void setCenterId(int centerId) {
      this.centerId = centerId;
   }

   public String getCoursetitle() {
      return coursetitle;
   }

   public void setCoursetitle(String coursetitle) {
      this.coursetitle = coursetitle;
   }

   public CharSequence toJson() {
      return "{\"centerId\": " + centerId + ",\"coursetitle\":\"" + coursetitle + "\"}";
   }
}
