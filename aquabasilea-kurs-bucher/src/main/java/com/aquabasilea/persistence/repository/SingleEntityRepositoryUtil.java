package com.aquabasilea.persistence.repository;

import com.aquabasilea.persistence.entity.base.BaseEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Iterator;
import java.util.UUID;

public class SingleEntityRepositoryUtil<T extends BaseEntity> {

   private SingleEntityRepositoryUtil() {
      // private
   }

   public static <T extends BaseEntity> T findFirstEntity(CrudRepository<T, UUID> crudRepository) {
      Iterable<T> weeklyCoursesDaoAll = crudRepository.findAll();
      Iterator<T> iterator = weeklyCoursesDaoAll.iterator();
      if (iterator.hasNext()) {
         return iterator.next();
      }
      return null;
   }
}

