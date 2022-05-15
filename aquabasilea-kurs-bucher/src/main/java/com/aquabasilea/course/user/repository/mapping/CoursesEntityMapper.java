package com.aquabasilea.course.user.repository.mapping;

import com.aquabasilea.course.user.Course;
import com.aquabasilea.persistence.entity.course.user.CourseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CoursesEntityMapper {

   @Mapping(target = "weeklyCoursesEntity", ignore = true)
   CourseEntity map2CourseEntity(Course course);

   @Mapping(target = "shiftCourseDateByDays", ignore = true)
   Course map2Course(CourseEntity courseEntity);
}