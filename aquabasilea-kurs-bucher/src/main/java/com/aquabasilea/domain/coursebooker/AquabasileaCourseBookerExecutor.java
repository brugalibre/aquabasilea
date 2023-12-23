package com.aquabasilea.domain.coursebooker;

import com.aquabasilea.application.config.logging.MdcConst;
import com.aquabasilea.domain.coursebooker.model.state.CourseBookingState;
import com.aquabasilea.domain.coursebooker.states.callback.CourseBookingStateChangedHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import static com.aquabasilea.domain.coursebooker.model.state.CourseBookingState.REFRESH_COURSES;
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
    private final String userId;
    private Future<?> courseBookerFuture;
    private boolean isPausing;

    /**
     * Creates a new {@link AquabasileaCourseBookerExecutor}
     * @param aquabasileaCourseBooker the {@link AquabasileaCourseBookerExecutor} which is controlled by this instance
     * @param userId the id of the user, the given {@link AquabasileaCourseBookerExecutor} belongs to
     */
    public AquabasileaCourseBookerExecutor(AquabasileaCourseBooker aquabasileaCourseBooker, String userId) {
        this.aquabasileaCourseBooker = aquabasileaCourseBooker;
        this.aquabasileaCourseBooker.addCourseBookingStateChangedHandler(this);
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        this.userId = userId;
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
        // Trying to move that 4 lines into a method will fail the aquabasileaCourseBooker to be executed. Somehow, that runnable is never called
        Runnable executeCourseBookerRunnable = () -> {
            MDC.put(MdcConst.USER_ID, userId);
            aquabasileaCourseBooker.handleCurrentState();
        };
        this.courseBookerFuture = scheduledExecutorService.submit(executeCourseBookerRunnable);
    }
}
