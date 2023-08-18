package com.aquabasilea.domain.coursebooker;

import com.aquabasilea.domain.coursebooker.states.CourseBookingState;
import com.aquabasilea.domain.coursebooker.states.callback.CourseBookingStateChangedHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import static com.aquabasilea.domain.coursebooker.states.CourseBookingState.REFRESH_COURSES;
import static java.util.Objects.requireNonNull;

/**
 * The {@link AquabasileaCourseBookerExecutor} is responsible for executing the {@link AquabasileaCourseBooker}
 * The {@link AquabasileaCourseBooker} does its own thing and changes its status. After each such status change the
 * AquabasileaCourseBookerController schedules the next iteration. Depending on if the CourseBooker is idle and has to
 * wait for the booking or if it has to find and get ready for the next course.
 */
public class AquabasileaCourseBookerExecutor implements CourseBookingStateChangedHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AquabasileaCourseBookerExecutor.class);
    private static final String MSG = "courseBookerFuture must not be null when we are in state " + REFRESH_COURSES;
    private final AquabasileaCourseBooker aquabasileaCourseBooker;
    private final ScheduledExecutorService scheduledExecutorService;
    private Future<?> courseBookerFuture;
    private boolean isPausing;

    public AquabasileaCourseBookerExecutor(AquabasileaCourseBooker aquabasileaCourseBooker) {
        this.aquabasileaCourseBooker = aquabasileaCourseBooker;
        this.aquabasileaCourseBooker.addCourseBookingStateChangedHandler(this);
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * If the current status is idle -> then the next execution is delayed according to the delay, which was evaluated
     * by de {@link AquabasileaCourseBooker}. If the current state is init, booking or dry_run, then the next cycle
     * is executed immediately
     *
     * @param courseBookingState the new state
     */
    @Override
    public void onCourseBookingStateChanged(CourseBookingState courseBookingState) {
        switch (courseBookingState) {
            case INIT, BOOKING, BOOKING_DRY_RUN, IDLE_BEFORE_BOOKING, IDLE_BEFORE_DRY_RUN -> cancelIfPausedAndExecuteCourseBooker();
            case REFRESH_COURSES -> cancel();// always cancel what the booker is doing - it's not the executors decision if a change is legit
            case PAUSED -> handlePausedState();
            case STOP -> scheduledExecutorService.shutdown();
            default -> LOGGER.info("Ignoring state " + courseBookingState);
        }
    }

    private void cancelIfPausedAndExecuteCourseBooker() {
        if (isPausing) {
            cancel();
            isPausing = false;
        }
        executeCourseBooker();
    }

    private void handlePausedState() {
        this.isPausing = true;
        cancel();
        executeCourseBooker();
    }

    private void cancel() {
        requireNonNull(courseBookerFuture, MSG).cancel(true);
    }

    private void executeCourseBooker() {
        this.courseBookerFuture = scheduledExecutorService.submit(aquabasileaCourseBooker::handleCurrentState);
    }
}
