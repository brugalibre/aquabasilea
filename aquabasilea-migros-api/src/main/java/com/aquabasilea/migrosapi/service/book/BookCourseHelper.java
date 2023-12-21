package com.aquabasilea.migrosapi.service.book;

import com.aquabasilea.migrosapi.model.book.CancelCourse;
import com.aquabasilea.migrosapi.model.book.response.MigrosBookCourseResponse;
import com.aquabasilea.migrosapi.api.v1.model.book.request.MigrosApiCancelCourseRequest;
import com.aquabasilea.migrosapi.api.v1.model.book.response.CourseBookResult;
import com.aquabasilea.migrosapi.api.v1.model.getcourse.response.MigrosApiBookCourseResponse;
import com.brugalibre.common.http.model.method.HttpMethod;
import com.brugalibre.common.http.model.request.HttpRequest;
import com.brugalibre.common.http.model.response.ResponseWrapper;
import com.brugalibre.util.file.json.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.aquabasilea.migrosapi.service.MigrosApiConst.CENTER_ID_PLACEHOLDER;
import static com.aquabasilea.migrosapi.service.MigrosApiConst.COURSE_ID_TAC_PLACEHOLDER;

/**
 * Contains specific logic for booking and canceling courses
 */
public class BookCourseHelper {

    private final String migrosCourseBookUrl;
    private final String migrosBookCourseRequestBody;
    private static final Logger LOG = LoggerFactory.getLogger(BookCourseHelper.class);

    public BookCourseHelper(String migrosCourseBookUrl, String migrosBookCourseRequestBody) {
        this.migrosCourseBookUrl = migrosCourseBookUrl;
        this.migrosBookCourseRequestBody = migrosBookCourseRequestBody;
    }

    /**
     * Returns a {@link HttpRequest} for cancel a booked course for the given cancel-request
     *
     * @param migrosApiCancelCourseRequest the {@link MigrosApiCancelCourseRequest} which contains the relevant information
     * @param authorization the value for the authorization header
     * @return a {@link HttpRequest} for cancel a booked course for the given cancel-request
     */
    public HttpRequest getCancelCourseRequest(MigrosApiCancelCourseRequest migrosApiCancelCourseRequest, String authorization) {
        CancelCourse cancelCourse = CancelCourse.of(migrosApiCancelCourseRequest.courseBookingId());
        String cancelCourseJson = JsonUtil.createJsonFromObject(cancelCourse);
        return HttpRequest.getHttpRequest(HttpMethod.DELETE, migrosCourseBookUrl)
                .withJsonBody(cancelCourseJson)
                .withAuthorization(authorization);
    }

    /**
     * @param authorization the value for the authorization header
     * @return the {@link HttpRequest} for retrieving all booked courses
     *
     */
    public HttpRequest getBookedCoursesRequest(String authorization) {
        return HttpRequest.getHttpRequest(HttpMethod.GET, migrosCourseBookUrl)
                .withAuthorization(authorization);
    }

    /**
     * Returns the {@link HttpRequest} for booking a specific course for the given center and course
     *
     * @param centerId the id of the center in which the course takes place
     * @param courseId the id of the course to book
     * @param authorization the value for the authorization header
     * @return the {@link HttpRequest} for booking a specific course for the given center-id and course-id
     */
    public HttpRequest getBookCourseHttpRequest(String centerId, String courseId, String authorization) {
        return HttpRequest.getHttpRequest(HttpMethod.POST, migrosCourseBookUrl)
                .withJsonBody(migrosBookCourseRequestBody
                        .replace(CENTER_ID_PLACEHOLDER, centerId)
                        .replace(COURSE_ID_TAC_PLACEHOLDER, courseId))
                .withAuthorization(authorization);
    }

    public MigrosApiBookCourseResponse unwrapAndCreateApiBookCourseResponse(ResponseWrapper<MigrosBookCourseResponse> migrosBookCourseResponseWrapper) {
        if (migrosBookCourseResponseWrapper.successful()) {
            MigrosBookCourseResponse migrosBookCourseResponse = migrosBookCourseResponseWrapper.httpResponse();
            return new MigrosApiBookCourseResponse(migrosBookCourseResponse.getCourseBookResult(), migrosBookCourseResponse.getMessage());
        } else if (migrosBookCourseResponseWrapper.exception() != null) {
            return new MigrosApiBookCourseResponse(CourseBookResult.COURSE_NOT_SELECTED_EXCEPTION_OCCURRED, migrosBookCourseResponseWrapper.exception().getMessage());
        }
        // Not successful but also no exception -> can occur if empty a body was received
        MigrosBookCourseResponse migrosBookCourseResponse = migrosBookCourseResponseWrapper.httpResponse();
        return new MigrosApiBookCourseResponse(getCourseBookResult(migrosBookCourseResponse),
                getErrorMsg(migrosBookCourseResponseWrapper.statusCode(), migrosBookCourseResponse));
    }

    private static String getErrorMsg(int statusCode, MigrosBookCourseResponse migrosBookCourseResponse) {
        if (migrosBookCourseResponse == null) {
            LOG.warn("No response available -> no error cause. Check previous logs!");
            return "Unbekannter Fehler (" + statusCode + ")!";
        }
        return migrosBookCourseResponse.getMessage();
    }

    private static CourseBookResult getCourseBookResult(MigrosBookCourseResponse migrosBookCourseResponse) {
        return migrosBookCourseResponse != null ? migrosBookCourseResponse.getCourseBookResult() : CourseBookResult.COURSE_NOT_BOOKABLE_TECHNICAL_ERROR;
    }
}
