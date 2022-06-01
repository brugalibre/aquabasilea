package com.aquabasilea.model.course.weeklycourses.repository.mapping;

import com.aquabasilea.model.course.weeklycourses.Course;
import com.aquabasilea.persistence.entity.course.weeklycourses.CourseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CoursesEntityMapper {

   @Mapping(target = "weeklyCoursesEntity", ignore = true)
   CourseEntity map2CourseEntity(Course course);

   @Mapping(target = "shiftCourseDateByDays", ignore = true)
   Course map2Course(CourseEntity courseEntity);
}