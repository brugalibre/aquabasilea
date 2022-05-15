package com.aquabasilea.course.user.repository.mapping;

import com.aquabasilea.course.user.WeeklyCourses;
import com.aquabasilea.persistence.entity.course.user.WeeklyCoursesEntity;
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