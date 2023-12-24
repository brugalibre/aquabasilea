package com.aquabasilea.domain.courselocation.model;

import com.brugalibre.common.domain.model.DomainModel;

public record CourseLocation(String id, String centerId, String name) implements DomainModel {

   /**
    * @return the technical id of this domain-model.
    * Note that this is <b>not</b> the center-id!
    */
   @Override
   public String getId() {
      return id;
   }
}
