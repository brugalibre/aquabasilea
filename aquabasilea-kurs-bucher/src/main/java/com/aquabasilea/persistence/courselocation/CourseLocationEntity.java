package com.aquabasilea.persistence.courselocation;

import com.brugalibre.common.domain.persistence.DomainEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@Entity
@Table(name = "courselocation")
public class CourseLocationEntity extends DomainEntity {

   @NotNull
   private String centerId;

   @NotNull
   private String name;

   public CourseLocationEntity() {
      super(null);
   }

   public String getCenterId() {
      return centerId;
   }

   public void setCenterId(String centerId) {
      this.centerId = centerId;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) return true;
      if (object == null || getClass() != object.getClass()) return false;
      if (!super.equals(object)) return false;
      CourseLocationEntity that = (CourseLocationEntity) object;
      return Objects.equals(name, that.name);
   }

   @Override
   public int hashCode() {
      return Objects.hash(super.hashCode(), name);
   }
}
