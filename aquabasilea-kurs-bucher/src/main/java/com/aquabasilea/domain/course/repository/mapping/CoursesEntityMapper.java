package com.aquabasilea.domain.course.repository.mapping;

import com.aquabasilea.domain.course.model.Course;
import com.aquabasilea.persistence.courses.CourseEntity;
import com.brugalibre.common.domain.mapper.CommonDomainModelMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CoursesEntityMapper extends CommonDomainModelMapper<Course, CourseEntity> {

   @Mapping(target = "weeklyCoursesEntity", ignore = true)
   CourseEntity map2DomainEntity(Course course);

   @Mapping(target = "shiftCourseDateByDays", ignore = true)
   Course map2DomainModel(CourseEntity courseEntity);
}