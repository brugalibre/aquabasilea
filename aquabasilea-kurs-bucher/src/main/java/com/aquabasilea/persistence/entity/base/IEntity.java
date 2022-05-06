package com.aquabasilea.persistence.entity.base;

/**
 * Super interface for all kind of entities
 * 
 * @author DStalder
 *
 */
public interface IEntity<T> {

   /**
    * @return the id of this {@link IEntity}
    */
   T getId();
}
