package com.aquabasilea.search;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A getter method annotated with the {@link SearchableAttribute} indicates
 * that this attribute can be used by the {@link ObjectTextSearch}
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SearchableAttribute {
}
