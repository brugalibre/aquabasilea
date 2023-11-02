package com.aquabasilea.application.initialize.coursedef;

import com.aquabasilea.application.initialize.common.InitType;
import com.aquabasilea.application.initialize.common.InitializeOrder;
import com.aquabasilea.application.initialize.api.Initializer;
import com.aquabasilea.application.initialize.api.UserAddedEvent;
import com.aquabasilea.domain.coursedef.update.CourseDefUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.aquabasilea.application.initialize.common.InitializationConst.COURSE_DEF_UPDATER;

@Service
@InitializeOrder(order = COURSE_DEF_UPDATER, type = {InitType.USER_ADDED, InitType.USER_ACTIVATED})
public class CourseDefUpdaterInitializer implements Initializer {
   private final CourseDefUpdater courseDefUpdater;

   @Autowired
   public CourseDefUpdaterInitializer(CourseDefUpdater courseDefUpdater) {
      this.courseDefUpdater = courseDefUpdater;
   }

   @Override
   public void initialize(UserAddedEvent userAddedEvent) {
      courseDefUpdater.startScheduler(userAddedEvent.userId());
   }
}
