package com.aquabasilea.domain.course.model;

import com.brugalibre.common.domain.model.DomainModel;

public record WeeklyCoursesOverview(WeeklyCourses weeklyCourses, Course currentCourse) implements DomainModel {
    @Override
    public String getId() {
        return weeklyCourses.getId();
    }
}
