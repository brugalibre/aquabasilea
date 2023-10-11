package com.aquabasilea.domain.coursebooker.states.idle;

import com.aquabasilea.domain.coursebooker.states.CourseBookingState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.aquabasilea.domain.coursebooker.states.CourseBookingState.INIT;

/**
 * The {@link IdleStateHandler} simply handles the states {@link CourseBookingState#IDLE_BEFORE_BOOKING}
 * and {@link CourseBookingState#IDLE_BEFORE_DRY_RUN}.
 * It pauses therefore the execution of the executing Thread for the given amount of time
 */
public class IdleStateHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(IdleStateHandler.class);
    private IdleContext currentIdleContext;

    /**
     * Handles the states {@link CourseBookingState#IDLE_BEFORE_BOOKING}
     * * and {@link CourseBookingState#IDLE_BEFORE_DRY_RUN}.
     *
     * @param idleContext the {@link IdleContext} which defines the time to wait
     * @return an {@link IdleStateResult} with the result of the idle state
     */
    public IdleStateResult handleIdleState(IdleContext idleContext) {
        this.currentIdleContext = idleContext;
        CourseBookingState nextState = idleContext.courseBookingState().next();
        try {
            LOGGER.info("Going idle for {}ms", idleContext.idleTime());
            Thread.sleep(idleContext.idleTime().toMillis());
            LOGGER.info("Done idle");
        } catch (InterruptedException e) {
            LOGGER.info("Idling was interrupted");
            nextState = INIT;
        } finally {
            this.currentIdleContext = null;
        }
        return IdleStateResult.of(nextState);
    }

    /**
     * @return <code>true</code> if this {@link IdleStateHandler} is pausing indefinitely or <code>false</code> if only temporary
     */
    public boolean isPausing(){
        return currentIdleContext != null && currentIdleContext.isPaused();
    }
}
