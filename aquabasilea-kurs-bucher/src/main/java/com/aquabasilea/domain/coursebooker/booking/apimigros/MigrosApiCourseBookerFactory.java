package com.aquabasilea.domain.coursebooker.booking.apimigros;

import com.aquabasilea.domain.coursebooker.states.booking.facade.AquabasileaCourseBookerFacade;

import java.time.Duration;
import java.util.function.Supplier;

public class MigrosApiCourseBookerFactory {
    private final MigrosApiProvider migrosApiProvider;

    public MigrosApiCourseBookerFactory(MigrosApiProvider migrosApiProvider) {
        this.migrosApiProvider = migrosApiProvider;
    }

    public AquabasileaCourseBookerFacade createMigrosApiCourseBookerImpl(String username, Supplier<char[]> userPassword, Supplier<Duration> duration2WaitUntilCourseBecomesBookable) {
        return new MigrosApiCourseBookerFacadeImpl(migrosApiProvider.getNewMigrosApi(), username, userPassword, duration2WaitUntilCourseBecomesBookable);
    }
}
