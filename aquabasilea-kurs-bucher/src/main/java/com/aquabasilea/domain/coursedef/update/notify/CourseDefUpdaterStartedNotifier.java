package com.aquabasilea.domain.coursedef.update.notify;

import com.aquabasilea.domain.coursedef.model.CourseDef;

/**
 * Used to propagate the change of certain {@link CourseDef}s for a certain user to other observers
 */
public interface CourseDefUpdaterStartedNotifier {

    /**
     * Is Called as soon as the {@link com.aquabasilea.domain.coursedef.update.CourseDefUpdater} has been scheduled
     *
     * @param onSchedulerStartContext the {@link OnSchedulerStartContext}
     */
    void onSchedulerStarted(OnSchedulerStartContext onSchedulerStartContext);
}
