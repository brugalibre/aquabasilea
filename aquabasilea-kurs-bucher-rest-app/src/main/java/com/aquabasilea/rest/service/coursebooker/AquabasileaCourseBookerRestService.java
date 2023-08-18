package com.aquabasilea.rest.service.coursebooker;

import com.aquabasilea.domain.course.Course;
import com.aquabasilea.domain.coursebooker.model.state.CourseBookingStateOverview;
import com.aquabasilea.domain.coursebooker.states.CourseBookingState;
import com.aquabasilea.rest.model.coursebooker.CourseBookerEndResultDto;
import com.aquabasilea.rest.model.coursebooker.CourseBookingStateDto;
import com.aquabasilea.service.coursebooker.AquabasileaCourseBookerService;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseBookingEndResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AquabasileaCourseBookerRestService {

    private final AquabasileaCourseBookerService aquabasileaCourseBookerService;

    @Autowired
    public AquabasileaCourseBookerRestService(AquabasileaCourseBookerService aquabasileaCourseBookerService) {
        this.aquabasileaCourseBookerService = aquabasileaCourseBookerService;
    }

    public boolean isPaused(String userId) {
        return aquabasileaCourseBookerService.isPaused(userId);
    }

    public CourseBookingStateDto getCourseBookingStateDto(String userId) {
        CourseBookingStateOverview courseBookingStateOverview = aquabasileaCourseBookerService.getCourseBookingState(userId);
        com.aquabasilea.rest.model.coursebooker.CourseBookingState state = map2State(courseBookingStateOverview.courseBookingState());
        return new CourseBookingStateDto(courseBookingStateOverview.msg(), state);
    }

    private com.aquabasilea.rest.model.coursebooker.CourseBookingState map2State(CourseBookingState courseBookingState) {
        return switch (courseBookingState) {
            case PAUSED -> com.aquabasilea.rest.model.coursebooker.CourseBookingState.PAUSED;
            case BOOKING, BOOKING_DRY_RUN -> com.aquabasilea.rest.model.coursebooker.CourseBookingState.BOOKING;
            case IDLE_BEFORE_BOOKING, IDLE_BEFORE_DRY_RUN, INIT, REFRESH_COURSES, STOP -> com.aquabasilea.rest.model.coursebooker.CourseBookingState.IDLE;
        };
    }

    public void pauseOrResume(String userId) {
        aquabasileaCourseBookerService.pauseOrResume(userId);
    }

    /**
     * Does a dry-run booking of the current Course. Additionally, all consumers are notified about the result
     *
     * @param courseId the id of the {@link Course} to book
     * @return a {@link CourseBookerEndResultDto} with details about the booking
     */
    public CourseBookerEndResultDto bookCourseDryRun(String userId, String courseId) {
        CourseBookingEndResult courseBookingEndResult = aquabasileaCourseBookerService.bookCourseDryRun(userId, courseId);
        return CourseBookerEndResultDto.of(courseBookingEndResult);
    }
}
