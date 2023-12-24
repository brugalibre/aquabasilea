package com.aquabasilea.domain.courselocation.mapper;

import com.aquabasilea.domain.courselocation.model.CourseLocation;
import com.aquabasilea.persistence.courselocation.CourseLocationEntity;
import com.brugalibre.common.domain.mapper.CommonDomainModelMapper;
import org.mapstruct.Mapper;

@Mapper
public interface CoursesLocationEntityMapper extends CommonDomainModelMapper<CourseLocation, CourseLocationEntity> {
// no-op
}