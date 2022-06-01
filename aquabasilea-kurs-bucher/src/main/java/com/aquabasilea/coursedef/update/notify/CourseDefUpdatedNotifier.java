package com.aquabasilea.coursedef.update.notify;

import com.aquabasilea.model.course.coursedef.CourseDef;

import java.util.List;

public interface CourseDefUpdatedNotifier {

   /**
    * Is called as soon as the {@link CourseDef}s has been updated
    *
    * @param updatedCourseDefs the new list with {@link CourseDef}s
    */
   void courseDefsUpdated(List<CourseDef> updatedCourseDefs);
}
