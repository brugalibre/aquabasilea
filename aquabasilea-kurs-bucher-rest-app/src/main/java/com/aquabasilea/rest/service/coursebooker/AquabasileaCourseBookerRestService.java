package com.aquabasilea.rest.service.coursebooker;

import com.aquabasilea.domain.course.model.Course;
import com.aquabasilea.domain.course.model.CourseComparator;
import com.aquabasilea.domain.coursebooker.booking.facade.model.CourseCancelResult;
import com.aquabasilea.domain.coursebooker.model.state.CourseBookingStateOverview;
import com.aquabasilea.domain.coursebooker.states.CourseBookingState;
import com.aquabasilea.rest.i18n.LocaleProvider;
import com.aquabasilea.rest.model.course.weeklycourses.CourseDto;
import com.aquabasilea.rest.model.coursebooker.CourseBookerEndResultDto;
import com.aquabasilea.rest.model.coursebooker.CourseBookingStateDto;
import com.aquabasilea.service.coursebooker.AquabasileaCourseBookerService;
import com.aquabasilea.web.bookcourse.impl.select.result.CourseBookingEndResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AquabasileaCourseBookerRestService {

    private final AquabasileaCourseBookerService aquabasileaCourseBookerService;
    private final LocaleProvider localeProvider;

    @Autowired
    public AquabasileaCourseBookerRestService(AquabasileaCourseBookerService aquabasileaCourseBookerService, LocaleProvider localeProvider) {
        this.aquabasileaCourseBookerService = aquabasileaCourseBookerService;
        this.localeProvider = localeProvider;
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

    /**
     * Returns for the user with the given id all booked {@link Course}s
     *
     * @param userId the id of the user
     * @return for the user with the given id all booked {@link Course}s
     */
    public List<CourseDto> getBookedCourses(String userId) {
        return aquabasileaCourseBookerService.getBookedCourses(userId)
                .stream()
                .sorted(new CourseComparator())
                .map(course -> CourseDto.of(course, false, localeProvider.getCurrentLocale()))
                .collect(Collectors.toList());
    }

    /**
     * Cancels the course which is associated with the given booking id and returns a {@link CourseCancelResult}
     *
     * @param userId    the id of the user for who the given booking is going to be canceled
     * @param bookingId the id of the booking which should be canceled
     * @return a {@link CourseCancelResult}
     */
    public CourseCancelResult cancelCourse(String userId, String bookingId) {
        return aquabasileaCourseBookerService.cancelBookedCourse(userId, bookingId);
    }
}
