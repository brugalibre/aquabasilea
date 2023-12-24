package com.aquabasilea.migrosapi.model.getcenters.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MigrosGetCentersResponse {
   private String formatId;
   private String formatKey;
   private String title;
   private List<MigrosResponseCenter> centers;

   public MigrosGetCentersResponse() {
      // default constructor for jackson
      this.centers = new ArrayList<>();
   }

   public List<MigrosResponseCenter> getCenters() {
      return centers;
   }

   public void setCenters(List<MigrosResponseCenter> centers) {
      this.centers = centers;
      setFormKeyToCenters();
   }

   public String getFormatId() {
      return formatId;
   }

   public void setFormatId(String formatId) {
      this.formatId = formatId;
   }

   public String getFormatKey() {
      return formatKey;
   }

   public void setFormatKey(String formatKey) {
      this.formatKey = formatKey;
      setFormKeyToCenters();
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   private void setFormKeyToCenters() {
      if (this.centers != null) {
         this.centers.forEach(migrosResponseCenter -> migrosResponseCenter.setFormatKey(this.formatKey));
      }
   }

   @Override
   public String toString() {
      return "MigrosGetCentersResponse{" +
              "formatId='" + formatId + '\'' +
              ", formatKey='" + formatKey + '\'' +
              ", title='" + title + '\'' +
              ", centers=" + centers +
              '}';
   }
}
