package com.aquabasilea.domain.admin.model;

import java.util.List;

public record AdminOverview(int totalAquabasileaCourseBooker, int totalBookingCounter,
                            double bookingSuccessRate, List<Course4AdminView> nextCurrentCourses) {
}
