package com.aquabasilea.domain.coursebooker.booking.apimigros;

import com.aquabasilea.domain.coursebooker.states.booking.facade.AquabasileaCourseBookerFacade;
import com.aquabasilea.service.coursebooker.migros.MigrosApiProvider;

import java.time.Duration;
import java.util.function.Supplier;

public class MigrosApiCourseBookerFacadeFactory {
    private final MigrosApiProvider migrosApiProvider;

    public MigrosApiCourseBookerFacadeFactory(MigrosApiProvider migrosApiProvider) {
        this.migrosApiProvider = migrosApiProvider;
    }

    public AquabasileaCourseBookerFacade createMigrosApiCourseBookerImpl(String username, Supplier<char[]> userPassword, Supplier<Duration> duration2WaitUntilCourseBecomesBookable) {
        return new MigrosApiCourseBookerFacadeImpl(migrosApiProvider.getMigrosApi(), username, userPassword, duration2WaitUntilCourseBecomesBookable);
    }
}
