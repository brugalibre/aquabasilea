package com.aquabasilea.persistence.entity.course.weeklycourses.dao;

import com.aquabasilea.persistence.entity.course.weeklycourses.WeeklyCoursesEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface WeeklyCoursesDao extends CrudRepository<WeeklyCoursesEntity, UUID> {
}
