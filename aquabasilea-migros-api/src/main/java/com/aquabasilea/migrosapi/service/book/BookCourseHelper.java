package com.aquabasilea.migrosapi.service.book;

import com.aquabasilea.migrosapi.model.book.CancelCourse;
import com.aquabasilea.migrosapi.v1.model.book.MigrosApiCancelCourseRequest;
import com.brugalibre.common.http.model.method.HttpMethod;
import com.brugalibre.common.http.model.request.HttpRequest;
import com.brugalibre.util.file.json.JsonUtil;

/**
 * Contains specific logic for booking and canceling courses
 */
public class BookCourseHelper {

    private final String migrosCourseBookUrl;

    public BookCourseHelper(String migrosCourseBookUrl) {
        this.migrosCourseBookUrl = migrosCourseBookUrl;
    }

    public HttpRequest getCancelCourseRequest(MigrosApiCancelCourseRequest migrosApiCancelCourseRequest) {
        CancelCourse cancelCourse = CancelCourse.of(migrosApiCancelCourseRequest.courseBookingId());
        String cancelCourseJson = JsonUtil.createJsonFromObject(cancelCourse);
        return HttpRequest.getHttpRequest(HttpMethod.DELETE, cancelCourseJson, migrosCourseBookUrl);
    }
}
