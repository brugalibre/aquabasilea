package com.aquabasilea.rest.service.coursedef;

import com.aquabasilea.coursedef.update.CourseDefUpdater;
import com.aquabasilea.coursedef.update.notify.CourseDefUpdatedNotifier;
import com.aquabasilea.model.course.CourseLocation;
import com.aquabasilea.model.course.coursedef.CourseDef;
import com.aquabasilea.model.course.coursedef.repository.CourseDefRepository;
import com.aquabasilea.rest.model.CourseLocationDto;
import com.aquabasilea.rest.model.course.coursedef.CourseDefDto;
import com.aquabasilea.search.ObjectTextSearch;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static java.util.Objects.isNull;

@Service
public class CourseDefService implements ApplicationListener<ContextRefreshedEvent>, CourseDefUpdatedNotifier {

   private final ObjectTextSearch objectTextSearch;
   private final CourseDefUpdater courseDefUpdater;
   private final CourseDefRepository courseDefRepository;
   private List<CourseDefDto> cachedCourseDefDtos;

   @Autowired
   public CourseDefService(CourseDefUpdater courseDefUpdater, CourseDefRepository courseDefRepository, ObjectTextSearch objectTextSearch) {
      this.courseDefUpdater = courseDefUpdater;
      this.courseDefUpdater.addCourseDefUpdatedNotifier(this);
      this.courseDefRepository = courseDefRepository;
      this.objectTextSearch = objectTextSearch;
      this.cachedCourseDefDtos = new ArrayList<>();
   }

   /**
    * Calls {@link CourseDefUpdater#startScheduler()}
    *
    * @param contextRefreshedEvent the {@link ContextRefreshedEvent}
    */
   @Override
   public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
      this.courseDefUpdater.startScheduler();
   }

   public boolean isCourseDefUpdateRunning() {
      return courseDefUpdater.isCourseDefUpdateRunning();
   }

   public void updateCourseDefs(List<CourseLocation> courseLocations) {
      courseDefUpdater.updateAquabasileaCourses(courseLocations);
   }

   public List<CourseDefDto> getCourseDefDtos4Filter(String filter) {
      List<CourseDefDto> allCourseDefDtos = getAllCourseDefDtos();

      if (isNull(filter)) {
         return allCourseDefDtos
                 .stream()
                 .sorted(Comparator.comparing(CourseDefDto::courseName))
                 .toList();
      }
      return objectTextSearch.getWeightedObjects4Filter(allCourseDefDtos, filter);
   }

   public List<CourseLocationDto> getCourseLocationsDtos() {
      return Arrays.stream(CourseLocation.values())
              .map(CourseLocationDto::of)
              .toList();
   }

   private synchronized List<CourseDefDto> getAllCourseDefDtos() {
      if (cachedCourseDefDtos.isEmpty()) {
         this.cachedCourseDefDtos = getAllCourseDefDtosFromRepository();
      }
      return cachedCourseDefDtos;
   }

   @NotNull
   private List<CourseDefDto> getAllCourseDefDtosFromRepository() {
      return courseDefRepository.findAllCourseDefs()
              .stream()
              .map(CourseDefDto::of)
              .toList();
   }

   @Override
   public void courseDefsUpdated(List<CourseDef> courseDefs) {
      this.cachedCourseDefDtos = courseDefs.stream()
              .map(CourseDefDto::of)
              .toList();
   }
}
