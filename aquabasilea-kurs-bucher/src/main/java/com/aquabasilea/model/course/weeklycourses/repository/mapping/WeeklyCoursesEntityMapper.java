package com.aquabasilea.model.course.weeklycourses.repository.mapping;

import com.aquabasilea.model.course.weeklycourses.WeeklyCourses;
import com.aquabasilea.persistence.entity.course.weeklycourses.WeeklyCoursesEntity;
import org.mapstruct.*;

@Mapper(uses = CoursesEntityMapper.class)
public interface WeeklyCoursesEntityMapper {

   @Mapping(target = "coursesEntities", source = "courses")
   WeeklyCoursesEntity map2WeeklyCoursesEntity(WeeklyCourses weeklyCourses);

   @AfterMapping
   default void setWeeklyCoursesEntity(@MappingTarget WeeklyCoursesEntity weeklyCoursesEntity) {
      weeklyCoursesEntity.getCoursesEntities()
              .forEach(courseEntity -> courseEntity.setWeeklyCoursesEntity(weeklyCoursesEntity));
   }

   @Mapping(target = "courses", source = "coursesEntities")
   WeeklyCourses map2WeeklyCourses(WeeklyCoursesEntity savedWeeklyCoursesEntity);
}