package com.aquabasilea.coursebooker.service.weeklycourses;

import com.aquabasilea.coursebooker.AquabasileaCourseBookerHolder;
import com.aquabasilea.coursebooker.model.course.weeklycourses.Course;
import com.aquabasilea.coursebooker.model.course.weeklycourses.WeeklyCourses;
import com.aquabasilea.coursebooker.model.course.weeklycourses.repository.WeeklyCoursesRepository;
import com.aquabasilea.coursedef.model.CourseDef;
import com.brugalibre.domain.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WeeklyCoursesService {

   public static final String WEEKLY_COURSES_SERVICE = "weeklyCoursesService";
   private final AquabasileaCourseBookerHolder aquabasileaCourseBookerHolder;
   private final WeeklyCoursesRepository weeklyCoursesRepository;

   @Autowired
   public WeeklyCoursesService(WeeklyCoursesRepository weeklyCoursesRepository, AquabasileaCourseBookerHolder aquabasileaCourseBookerHolder) {
      this.aquabasileaCourseBookerHolder = aquabasileaCourseBookerHolder;
      this.weeklyCoursesRepository = weeklyCoursesRepository;
   }

   /**
    * Returns the {@link WeeklyCourses} instance associated with an {@link User} with the given id
    *
    * @param userId the technical id of the {@link User}
    * @return the {@link WeeklyCourses} instance associated with an {@link User} with the given id
    */
   public WeeklyCourses getByUserId(String userId) {
      return weeklyCoursesRepository.getByUserId(userId);
   }

   /**
    * Saves the given {@link WeeklyCourses}
    *
    * @param weeklyCourses the {@link WeeklyCourses} to save
    */
   public void save(WeeklyCourses weeklyCourses) {
      weeklyCoursesRepository.save(weeklyCourses);
   }

   /**
    * This Method checks for each {@link Course} if it has an equivalent aquabasilea course aka
    * {@link CourseDef} and updates the attribute {@link Course#getHasCourseDef()}
    *
    * @param userId     the technical id of the {@link User} for which the {@link CourseDef}s has been updated
    * @param courseDefs the new {@link CourseDef} which are extracted from the aquabasilea course page
    */
   public void updateCoursesAfterCourseDefUpdate(String userId, List<CourseDef> courseDefs) {
      WeeklyCourses weeklyCourses = weeklyCoursesRepository.getByUserId(userId);
      weeklyCourses.updateCoursesHasCourseDef(courseDefs);
      changeWeeklyCourseAndRefreshCourseBooker(userId, weeklyCourses);
   }

   private void changeWeeklyCourseAndRefreshCourseBooker(String userId, WeeklyCourses weeklyCourses) {
      weeklyCoursesRepository.save(weeklyCourses);
      aquabasileaCourseBookerHolder.getForUserId(userId).refreshCourses();
   }
}
