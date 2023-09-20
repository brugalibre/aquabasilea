package com.aquabasilea.migrosapi.service.book;

import com.aquabasilea.migrosapi.model.book.CancelCourse;
import com.aquabasilea.migrosapi.v1.model.book.MigrosApiCancelCourseRequest;
import com.brugalibre.common.http.model.method.HttpMethod;
import com.brugalibre.common.http.model.request.HttpRequest;
import com.brugalibre.util.file.json.JsonUtil;

import static com.aquabasilea.migrosapi.service.MigrosApiConst.CENTER_ID_PLACEHOLDER;
import static com.aquabasilea.migrosapi.service.MigrosApiConst.COURSE_ID_TAC_PLACEHOLDER;

/**
 * Contains specific logic for booking and canceling courses
 */
public class BookCourseHelper {

    private final String migrosCourseBookUrl;
    private final String migrosBookCourseRequestBody;

    public BookCourseHelper(String migrosCourseBookUrl, String migrosBookCourseRequestBody) {
        this.migrosCourseBookUrl = migrosCourseBookUrl;
        this.migrosBookCourseRequestBody = migrosBookCourseRequestBody;
    }

    /**
     * Returns a {@link HttpRequest} for cancel a booked course for the given cancel-request
     *
     * @param migrosApiCancelCourseRequest the {@link MigrosApiCancelCourseRequest} which contains the relevant information
     * @return a {@link HttpRequest} for cancel a booked course for the given cancel-request
     */
    public HttpRequest getCancelCourseRequest(MigrosApiCancelCourseRequest migrosApiCancelCourseRequest) {
        CancelCourse cancelCourse = CancelCourse.of(migrosApiCancelCourseRequest.courseBookingId());
        String cancelCourseJson = JsonUtil.createJsonFromObject(cancelCourse);
        return HttpRequest.getHttpRequest(HttpMethod.DELETE, cancelCourseJson, migrosCourseBookUrl);
    }

    /**
     * @return the {@link HttpRequest} for retrieving all booked courses
     */
    public HttpRequest getBookedCoursesRequest() {
        return HttpRequest.getHttpGetRequest(migrosCourseBookUrl);
    }

    /**
     * Returns the {@link HttpRequest} for booking a specific course for the given center and course
     *
     * @param centerId the id of the center in which the course takes place
     * @param courseId the id of the course to book
     * @return the {@link HttpRequest} for booking a specific course for the given center-id and course-id
     */
    public HttpRequest getBookCourseHttpRequest(String centerId, String courseId) {
        return HttpRequest.getHttpPostRequest(migrosBookCourseRequestBody
                .replace(CENTER_ID_PLACEHOLDER, centerId)
                .replace(COURSE_ID_TAC_PLACEHOLDER, courseId), migrosCourseBookUrl);
    }

}