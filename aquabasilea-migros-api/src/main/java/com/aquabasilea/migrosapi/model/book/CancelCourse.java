package com.aquabasilea.migrosapi.model.book;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record CancelCourse(String language, String bookingIdTac) {

    public static CancelCourse of(String bookingIdTac) {
        return new CancelCourse("de", bookingIdTac);
    }
}
