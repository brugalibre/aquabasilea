package com.aquabasilea.domain.course.repository.mapping;

import com.aquabasilea.domain.course.model.Course;
import com.aquabasilea.domain.course.model.CourseLocation;
import com.aquabasilea.migrosapi.v1.model.getcourse.response.MigrosCourse;
import com.aquabasilea.persistence.courses.CourseEntity;
import com.brugalibre.common.domain.mapper.CommonDomainModelMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface CoursesEntityMapper extends CommonDomainModelMapper<Course, CourseEntity> {

   @Mapping(target = "weeklyCoursesEntity", ignore = true)
   CourseEntity map2DomainEntity(Course course);

   @Mapping(target = "bookingIdTac", ignore = true)
   @Mapping(target = "shiftCourseDateByDays", ignore = true)
   Course map2DomainModel(CourseEntity courseEntity);

   List<Course> mapMigrosCourses2Courses(List<MigrosCourse> migrosCourses);

   @Mapping(target = "isPaused", ignore = true)
   @Mapping(target = "id", ignore = true)
   @Mapping(target = "shiftCourseDateByDays", ignore = true)
   @Mapping(target = "hasCourseDef", ignore = true)
   @Mapping(source = "centerId", target = "courseLocation", qualifiedByName = "mapCenterId2CourseLocation")
   Course mapMigrosCourse2Course(MigrosCourse migrosCourse);

   @Named("mapCenterId2CourseLocation")
   default CourseLocation mapCenterId2CourseLocation(String centerId) {
      return CourseLocation.fromId(centerId);
   }
}