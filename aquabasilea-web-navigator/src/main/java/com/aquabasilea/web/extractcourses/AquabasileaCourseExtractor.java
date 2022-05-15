package com.aquabasilea.web.extractcourses;

import com.aquabasilea.web.extractcourses.model.AquabasileaCourse;
import com.aquabasilea.web.model.CourseLocation;
import com.aquabasilea.web.extractcourses.model.ExtractedAquabasileaCourses;

import java.util.List;

/**
 * The {@link AquabasileaCourseExtractor} extracts the courses on the course overview page
 */
public interface AquabasileaCourseExtractor {

   /**
    * Extracts all courses which takes place at the given list with course locations
    * <p>
    * Note: the default course location 'Migros Fitnesscenter Aquabasilea' is always included - for the sake of this app!
    *
    * @param courseLocations the course location to filter
    * @return a {@link ExtractedAquabasileaCourses} which contains a list with {@link AquabasileaCourse}s
    */
   ExtractedAquabasileaCourses extractAquabasileaCourses(List<CourseLocation> courseLocations);
}
