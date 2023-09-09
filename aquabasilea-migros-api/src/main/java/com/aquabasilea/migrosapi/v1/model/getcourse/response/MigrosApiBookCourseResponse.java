package com.aquabasilea.migrosapi.v1.model.getcourse.response;

import com.aquabasilea.migrosapi.v1.model.book.CourseBookResult;
import com.aquabasilea.migrosapi.v1.model.book.MigrosApiBookCourseRequest;
import com.aquabasilea.migrosapi.v1.model.security.AuthenticationContainer;
import com.aquabasilea.migrosapi.v1.service.MigrosApi;

/**
 * The {@link MigrosApiBookCourseResponse} defines the response from the {@link MigrosApi} for a {@link MigrosApi#bookCourse(AuthenticationContainer, MigrosApiBookCourseRequest)} call
 */
public record MigrosApiBookCourseResponse(CourseBookResult courseBookResult, String errorMsg) {
}
