package com.aquabasilea.migrosapi.model.getcenters.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

/**
 * A {@link MigrosResponseCenter} represents a migros-course when receiving a course via the migros-api
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class MigrosResponseCenter {
   private int centerId;
   private String formatKey;
   private String title;

   public MigrosResponseCenter() {
      // empty for jackson
   }

   public int getCenterId() {
      return centerId;
   }

   public void setCenterId(int centerId) {
      this.centerId = centerId;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getFormatKey() {
      return formatKey;
   }

   public void setFormatKey(String formatKey) {
      this.formatKey = formatKey;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) return true;
      if (object == null || getClass() != object.getClass()) return false;
      MigrosResponseCenter that = (MigrosResponseCenter) object;
      return centerId == that.centerId;
   }

   @Override
   public int hashCode() {
      return Objects.hash(centerId);
   }

   @Override
   public String toString() {
      return "MigrosResponseCenter{" +
              "centerId=" + centerId +
              ", title='" + title + '\'' +
              ", formatKey='" + formatKey + '\'' +
              '}';
   }
}
