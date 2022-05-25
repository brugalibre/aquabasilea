package com.aquabasilea.model;

import static java.util.Objects.nonNull;

public class AbstractDomainModel {
   protected String id;

   public String getId() {
      return id;
   }

   public void setId(String id) {
      if (nonNull(id)) {
         this.id = id;
      }
   }
}
