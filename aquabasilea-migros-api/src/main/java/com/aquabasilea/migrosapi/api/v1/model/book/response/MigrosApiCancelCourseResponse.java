package com.aquabasilea.migrosapi.api.v1.model.book.response;

import com.aquabasilea.migrosapi.model.book.response.MigrosCancelCourseResponse;

public record MigrosApiCancelCourseResponse(CourseCancelResult courseCancelResult) {
    public static MigrosApiCancelCourseResponse of(MigrosCancelCourseResponse migrosCancelCourseResponse, String bookingIdTac) {
        CourseCancelResult courseCancelResult = getCourseCancelResult(migrosCancelCourseResponse, bookingIdTac);
        return new MigrosApiCancelCourseResponse(courseCancelResult);
    }

    private static CourseCancelResult getCourseCancelResult(MigrosCancelCourseResponse migrosCancelCourseResponse, String expectedBookingIdTac) {
        if (requestWasSuccessful(migrosCancelCourseResponse, expectedBookingIdTac)) {
            return CourseCancelResult.COURSE_CANCELED;
        }
        return CourseCancelResult.COURSE_CANCEL_FAILED;
    }

    private static boolean requestWasSuccessful(MigrosCancelCourseResponse migrosCancelCourseResponse, String expectedBookingIdTac) {
        return migrosCancelCourseResponse != null
                && migrosCancelCourseResponse.isSuccessful(expectedBookingIdTac);
    }
}
