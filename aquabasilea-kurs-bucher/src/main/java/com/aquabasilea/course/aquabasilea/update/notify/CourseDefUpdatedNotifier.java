package com.aquabasilea.course.aquabasilea.update.notify;

import com.aquabasilea.course.aquabasilea.CourseDef;

import java.util.List;

public interface CourseDefUpdatedNotifier {

   /**
    * Is called as soon as the {@link CourseDef}s has been updated
    *
    * @param updatedCourseDefs the new list with {@link CourseDef}s
    */
   void courseDefsUpdated(List<CourseDef> updatedCourseDefs);
}
