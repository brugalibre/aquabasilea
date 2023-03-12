package com.aquabasilea.migrosapi.model.getcourse.response.api;

import com.aquabasilea.migrosapi.model.book.api.CourseBookResult;
import com.aquabasilea.migrosapi.model.book.api.MigrosApiBookCourseRequest;
import com.aquabasilea.migrosapi.model.security.api.AuthenticationContainer;
import com.aquabasilea.migrosapi.service.MigrosApi;

/**
 * The {@link MigrosApiBookCourseResponse} defines the response from the {@link MigrosApi} for a {@link MigrosApi#bookCourse(AuthenticationContainer, MigrosApiBookCourseRequest)} call
 */
public record MigrosApiBookCourseResponse(CourseBookResult courseBookResult, String errorMsg) {
}
