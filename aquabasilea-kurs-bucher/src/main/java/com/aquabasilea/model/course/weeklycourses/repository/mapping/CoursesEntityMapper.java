package com.aquabasilea.model.course.weeklycourses.repository.mapping;

import com.aquabasilea.model.course.weeklycourses.Course;
import com.aquabasilea.persistence.entity.course.weeklycourses.CourseEntity;
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