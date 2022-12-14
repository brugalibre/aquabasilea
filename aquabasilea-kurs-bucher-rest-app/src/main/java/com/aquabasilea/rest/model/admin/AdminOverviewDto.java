package com.aquabasilea.rest.model.admin;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record AdminOverviewDto(int totalAquabasileaCourseBooker, int totalBookingCounter,
                               double bookingSuccessRate, String uptimeRepresentation,
                               List<Course4AdminViewDto> nextCurrentCourses) {
}
