package com.aquabasilea.service.courses;

import com.aquabasilea.domain.coursebooker.AquabasileaCourseBooker;
import com.aquabasilea.domain.coursebooker.AquabasileaCourseBookerHolder;
import com.aquabasilea.domain.course.Course;
import com.aquabasilea.domain.course.WeeklyCourses;
import com.aquabasilea.domain.course.WeeklyCoursesOverview;
import com.aquabasilea.domain.course.repository.WeeklyCoursesRepository;
import com.aquabasilea.domain.coursedef.model.CourseDef;
import com.brugalibre.domain.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WeeklyCoursesService {
   private static final Logger LOG = LoggerFactory.getLogger(WeeklyCoursesService.class);

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
    * Returns the {@link WeeklyCoursesOverview} instance associated with an {@link User} with the given id
    *
    * @param userId the technical id of the {@link User}
    * @return the {@link WeeklyCoursesOverview} instance associated with an {@link User} with the given id
    */
   public WeeklyCoursesOverview getWeeklyCoursesOverviewByUserId(String userId) {
      WeeklyCourses weeklyCourses = weeklyCoursesRepository.getByUserId(userId);
      Course currentCourse = getAquabasileaCourseBooker4CurrentUser(userId).getCurrentCourse();
      return new WeeklyCoursesOverview(weeklyCourses, currentCourse);
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
    * Adds the given {@link Course} to the {@link WeeklyCourses} for the given user id
    *
    * @param course the course to add
    * @param userId the id of the user
    */
   public void addCourse(Course course, String userId) {
      LOG.info("Add course {}", course);
      WeeklyCourses weeklyCourses = getByUserId(userId);
      weeklyCourses.addCourse(course);
      changeWeeklyCourseAndRefreshCourseBooker(weeklyCourses, userId);
   }

   /**
    * Pauses the {@link Course} for the given id and user id
    *
    * @param courseId the id of the Course to pause
    * @param userId   the id of the user this course belongs to
    */
   public void pauseResumeCourse(String courseId, String userId) {
      LOG.info("Pausing / resuming course {}", courseId);
      WeeklyCourses weeklyCourses = getByUserId(userId);
      weeklyCourses.pauseResumeCourse(courseId);
      changeWeeklyCourseAndRefreshCourseBooker(weeklyCourses, userId);
   }

   /**
    * Deletes the {@link Course} for the given id and user id
    *
    * @param courseId2Delete the id of the Course to pause
    * @param userId          the id of the user this course belongs to
    */
   public void deleteCourseById(String courseId2Delete, String userId) {
      LOG.info("Delete course with id {}", courseId2Delete);
      WeeklyCourses weeklyCourses = getByUserId(userId);
      weeklyCourses.removeCourseById(courseId2Delete);
      changeWeeklyCourseAndRefreshCourseBooker(weeklyCourses, userId);
   }

   private void changeWeeklyCourseAndRefreshCourseBooker(WeeklyCourses weeklyCourses, String userId) {
      AquabasileaCourseBooker aquabasileaCourseBooker = getAquabasileaCourseBooker4CurrentUser(userId);
      save(weeklyCourses);
      aquabasileaCourseBooker.refreshCourses();
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

   private AquabasileaCourseBooker getAquabasileaCourseBooker4CurrentUser(String userId) {
      return aquabasileaCourseBookerHolder.getForUserId(userId);
   }

}
