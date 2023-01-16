package com.aquabasilea.migrosapi.mapper;

import com.aquabasilea.migrosapi.model.response.MigrosResponseCourse;
import com.aquabasilea.migrosapi.model.response.api.MigrosCourse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public abstract class MigrosCourseMapper {

   public abstract List<MigrosCourse> mapToMigrosCourses(List<MigrosResponseCourse> migrosResponseCourses);

   @Mapping(target = "courseName", source = "title")
   @Mapping(target = "courseInstructor", source = "instructor")
   @Mapping(target = "courseDate", source = "start"/*, dateFormat = "yyyy-mm-ddThh:MM:ss"*/)
   public abstract MigrosCourse mapToMigrosCourse(MigrosResponseCourse migrosResponseCourse);

}
