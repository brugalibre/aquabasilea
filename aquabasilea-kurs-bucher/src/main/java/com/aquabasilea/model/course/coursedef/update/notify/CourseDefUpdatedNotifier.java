package com.aquabasilea.model.course.coursedef.update.notify;

import com.aquabasilea.model.course.coursedef.CourseDef;
import com.brugalibre.domain.user.model.User;

import java.util.List;

/**
 * Used to propagate the change of certain {@link CourseDef}s for a certain user to other observers
 */
public interface CourseDefUpdatedNotifier {

   /**
    * Is called as soon as the {@link CourseDef}s has been updated for the given user-id
    *
    * @param userId            the technical id of the {@link User} for which the {@link CourseDef} as been updated
    * @param updatedCourseDefs the new list with {@link CourseDef}s
    */
   void courseDefsUpdated(String userId, List<CourseDef> updatedCourseDefs);
}
