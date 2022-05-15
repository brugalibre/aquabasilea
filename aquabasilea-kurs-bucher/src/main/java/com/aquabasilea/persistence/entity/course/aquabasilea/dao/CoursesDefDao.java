package com.aquabasilea.persistence.entity.course.aquabasilea.dao;

import com.aquabasilea.persistence.entity.course.aquabasilea.CourseDefEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface CoursesDefDao extends CrudRepository<CourseDefEntity, UUID> {
}
