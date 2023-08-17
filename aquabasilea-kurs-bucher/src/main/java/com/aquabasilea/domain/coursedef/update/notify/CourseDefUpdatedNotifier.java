package com.aquabasilea.domain.coursedef.update.notify;

import com.aquabasilea.domain.coursedef.model.CourseDef;

/**
 * Used to propagate the change of certain {@link CourseDef}s for a certain user to other observers
 */
public interface CourseDefUpdatedNotifier {

   /**
    * Is called as soon as the {@link CourseDef}s has been updated for the given user-id
    *
    * @param onCourseDefsUpdatedContext the {@link OnCourseDefsUpdatedContext} with information about the updated {@link CourseDef}s
    */
   void courseDefsUpdated(OnCourseDefsUpdatedContext onCourseDefsUpdatedContext);
}
