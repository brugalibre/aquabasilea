package com.aquabasilea.rest.service;

import com.aquabasilea.coursebooker.AquabasileaCourseBooker;
import com.aquabasilea.model.course.coursedef.CourseDef;
import com.aquabasilea.model.course.weeklycourses.Course;
import com.aquabasilea.model.course.weeklycourses.WeeklyCourses;
import com.aquabasilea.model.course.weeklycourses.repository.WeeklyCoursesRepository;
import com.aquabasilea.rest.i18n.LocalProvider;
import com.aquabasilea.rest.model.course.weeklycourses.CourseDto;
import com.aquabasilea.rest.model.course.weeklycourses.WeeklyCoursesDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

@Service
public class WeeklyCoursesService {

   private static final Logger LOG = LoggerFactory.getLogger(WeeklyCoursesService.class);
   private final AquabasileaCourseBooker aquabasileaCourseBooker;
   private final WeeklyCoursesRepository weeklyCoursesRepository;
   private final LocalProvider localProvider;

   @Autowired
   public WeeklyCoursesService(WeeklyCoursesRepository weeklyCoursesRepository, AquabasileaCourseBooker aquabasileaCourseBooker, LocalProvider localProvider) {
      this.aquabasileaCourseBooker = aquabasileaCourseBooker;
      this.weeklyCoursesRepository = weeklyCoursesRepository;
      this.localProvider = localProvider;
   }

   public void addCourse(CourseDto courseDto) {
      LOG.info("Add course {}", courseDto);
      WeeklyCourses weeklyCourses = weeklyCoursesRepository.findFirstWeeklyCourses();
      weeklyCourses.addCourse(CourseDto.map2Course(courseDto, localProvider.getCurrentLocale()));
      changeWeeklyCourseAndRefreshCourseBooker(weeklyCourses);
   }

   public void pauseResumeCourse(String courseId) {
      LOG.info("Pausing / resuming course {}", courseId);
      WeeklyCourses weeklyCourses = weeklyCoursesRepository.findFirstWeeklyCourses();
      weeklyCourses.pauseResumeCourse(courseId);
      changeWeeklyCourseAndRefreshCourseBooker(weeklyCourses);
   }

   public void deleteCourseById(String courseId2Delete) {
      LOG.info("Delete course with id {}", courseId2Delete);
      WeeklyCourses weeklyCourses = weeklyCoursesRepository.findFirstWeeklyCourses();
      weeklyCourses.removeCourseById(courseId2Delete);
      changeWeeklyCourseAndRefreshCourseBooker(weeklyCourses);
   }

   public WeeklyCoursesDto getWeeklyCoursesDto() {
      return WeeklyCoursesDto.of(weeklyCoursesRepository.findFirstWeeklyCourses(),
              aquabasileaCourseBooker.getCurrentCourse(), localProvider.getCurrentLocale());
   }

   /**
    * This Method checks for each {@link Course} if it has an equivalent aquabasilea course aka
    * {@link CourseDef} and updates the attribute {@link Course#getHasCourseDef()}
    *
    * @param courseDefs the new {@link CourseDef} which are extracted from the aquabasilea course page
    */
   public void updateCoursesAfterCourseDefUpdate(List<CourseDef> courseDefs) {
      WeeklyCourses weeklyCourses = weeklyCoursesRepository.findFirstWeeklyCourses();
      weeklyCourses.getCourses()
              .stream()
              .map(setHasCourseDef(courseDefs))
              .forEach(weeklyCourses::changeCourse);
      changeWeeklyCourseAndRefreshCourseBooker(weeklyCourses);
   }

   private void changeWeeklyCourseAndRefreshCourseBooker(WeeklyCourses weeklyCourses) {
      weeklyCoursesRepository.save(weeklyCourses);
      aquabasileaCourseBooker.refreshCourses();
   }

   private static Function<Course, Course> setHasCourseDef(List<CourseDef> courseDefs) {
      return course -> {
         course.setHasCourseDef(existsCourseDef4Course(course, courseDefs));
         if (!course.getHasCourseDef()) {
            course.shiftCourseDateByDays(7);
            course.setHasCourseDef(existsCourseDef4Course(course, courseDefs));
         }
         return course;
      };
   }

   private static boolean existsCourseDef4Course(Course course, List<CourseDef> courseDefs) {
      return courseDefs.stream()
              .anyMatch(courseDef -> courseDef.courseName().equals(course.getCourseName())
                      && courseDef.courseLocation().equals(course.getCourseLocation())
                      && courseDef.courseDate().equals(course.getCourseDate().toLocalDate())
                      && courseDef.timeOfTheDay().equals(course.getTimeOfTheDay())
              );
   }

}
