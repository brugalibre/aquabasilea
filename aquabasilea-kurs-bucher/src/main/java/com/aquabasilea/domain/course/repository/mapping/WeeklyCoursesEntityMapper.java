package com.aquabasilea.domain.course.repository.mapping;

import com.aquabasilea.domain.course.model.WeeklyCourses;
import com.aquabasilea.persistence.courses.WeeklyCoursesEntity;
import com.brugalibre.common.domain.mapper.CommonDomainModelMapper;
import com.brugalibre.domain.user.mapper.UserEntityMapper;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(uses = {CoursesEntityMapper.class, UserEntityMapper.class})
public interface WeeklyCoursesEntityMapper extends CommonDomainModelMapper<WeeklyCourses, WeeklyCoursesEntity> {

   @AfterMapping
   default void setWeeklyCoursesEntity(@MappingTarget WeeklyCoursesEntity weeklyCoursesEntity) {
      weeklyCoursesEntity.getCourses()
              .forEach(courseEntity -> courseEntity.setWeeklyCoursesEntity(weeklyCoursesEntity));
   }
}