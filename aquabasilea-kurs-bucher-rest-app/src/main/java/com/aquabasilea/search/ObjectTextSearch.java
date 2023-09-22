package com.aquabasilea.search;

import com.aquabasilea.reflection.ReflectionUtil;
import com.aquabasilea.rest.i18n.LocaleProvider;
import com.aquabasilea.util.DateUtil;
import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import static java.util.Objects.nonNull;

/**
 * The {@link ObjectTextSearch} provides searching functionality, which allows to search for a {@link Object}s with
 * one or more attributes using a certain filter. For each attributes of this Object, which are annotated with the {@link SearchableAttribute},
 * is a {@link JaroWinklerDistance} created to determine the weight of the attribute. If the attribute is a String, then it's directly used.
 * If it's rather a complex Type, then this type is searched recursively in order to extract all searchable attributes.
 * <p>
 * The given list is then sorted according the weight of all {@link Object} and only the best matches are returned
 * Attention: the logic is bounded at v. commons-text:1.9. The JaroWinklerDistance works different and this leads
 * to different results. Also, the weighted results needs to be inverted with v1.9
 */
@Component
public class ObjectTextSearch {
   private static final String WORD_SEPARATOR = " ";

   private final LocaleProvider localeProvider;
   private final double matchThreshold;

   /**
    * For testing purpose only!
    * A match below 66.7% is not really representative sometimes... So that's why we only want to count those above 66.7%
    * @param matchThreshold the value a course must match to be selected
    */
   @Autowired
   ObjectTextSearch(@Value("${application.search.maxThreshold:0.667}") double matchThreshold, LocaleProvider localeProvider) {
      this.matchThreshold = matchThreshold;
      this.localeProvider = localeProvider;
   }

   /**
    * Takes the given objects and returns a filtered list of them, whereas the elements on top matches the most with the given
    * filter and the elements on the bottom matches the least
    *
    * @param objects2Filter the objects to filter
    * @param filter         the filter existing of one or more words
    * @param <T>            the actual type of the given List of Objects
    * @return a filtered and ordered list of matches, limited to <code>maxFilterResults</code> results
    */
   @SuppressWarnings("unchecked")
   public <T> List<T> getWeightedObjects4Filter(List<T> objects2Filter, String filter) {
      return objects2Filter.stream()
              .map(weightAndMap2WeightedObjects(filter))
              .sorted(Comparator.comparing(WeightedObject::weight).reversed())
              .filter(WeightedObject::hasWeight)
              .map(WeightedObject::object)
              .map(o -> (T) o)
              .toList();
   }

   private Function<Object, WeightedObject> weightAndMap2WeightedObjects(String filter) {
      return object -> {
         double totalCourseDefDtoWeight = 0.0;
         for (String singleWordFilter : filter.split(WORD_SEPARATOR)) {
            for (String searchableObjectAttr : getSearchableAttrsFromObject(object)) {
               totalCourseDefDtoWeight = totalCourseDefDtoWeight + applyJaroWinklerDistance(searchableObjectAttr, singleWordFilter);
            }
         }
         return new WeightedObject(object, totalCourseDefDtoWeight);
      };
   }

   /**
    * The JaroWinklerDistance works best, if there are no spaces in the word. So e.g. the objects searchable attribute
    * 'the foo' should be split in 'the' and 'foo' in order to increase accuracy when searching with the filter 'foo test'
    */
   private List<String> getSearchableAttrsFromObject(Object object) {
      return extractAnnotatedSearchableAttrsFrom(object).stream()
              .map(searchableAttr -> searchableAttr.split(WORD_SEPARATOR))
              .map(Arrays::asList)
              .flatMap(List::stream)
              .toList();
   }

   private List<String> extractAnnotatedSearchableAttrsFrom(Object object) {
      List<String> searchableObjectAttrs = new ArrayList<>();
      for (Field declaredField : object.getClass().getDeclaredFields()) {
         SearchableAttribute searchableAttributeAnnotation = declaredField.getAnnotation(SearchableAttribute.class);
         if (nonNull(searchableAttributeAnnotation)) {
            searchableObjectAttrs.addAll(extractAnnotatedSearchableAttrsFromObjectAndField(object, declaredField));
         }
      }
      return searchableObjectAttrs;
   }

   private List<String> extractAnnotatedSearchableAttrsFromObjectAndField(Object object, Field declaredField) {
      List<String> searchableObjectAttrs = new ArrayList<>();
      Object fieldValue;
      try {
         declaredField.setAccessible(true);
         fieldValue = declaredField.get(object);
         if (fieldValue instanceof String fieldValueAString) {
            searchableObjectAttrs.add(fieldValueAString);
         } else if (fieldValue instanceof LocalDateTime) {
            extractValueFromLocalDateTime(searchableObjectAttrs, (LocalDateTime) fieldValue);
         } else if (!ReflectionUtil.isPrimitive(fieldValue)) {
            searchableObjectAttrs.addAll(getSearchableAttrsFromObject(fieldValue));
         }
      } catch (IllegalArgumentException | IllegalAccessException e) {
         throw new IllegalStateException(e);
      }
      return searchableObjectAttrs;
   }

   private void extractValueFromLocalDateTime(List<String> searchableObjectAttrs, LocalDateTime fieldValue) {
      extractValueFromLocalDate(searchableObjectAttrs, fieldValue.toLocalDate());
   }

   private void extractValueFromLocalDate(List<String> searchableObjectAttrs, LocalDate courseDate) {
      searchableObjectAttrs.add(DateUtil.toString(courseDate, localeProvider.getCurrentLocale()));
   }

   private Double applyJaroWinklerDistance(String courseDefRepresentationElement, String singleWordFilter) {
      Double matchValue = new JaroWinklerDistance().apply(courseDefRepresentationElement.toLowerCase(), singleWordFilter.toLowerCase());
      if (matchValue <= matchThreshold) {
         return 0.0;
      }
      return matchValue;
   }

   record WeightedObject(Object object, double weight) {

      public boolean hasWeight() {
         return weight > 0.0;
      }
   }

}
