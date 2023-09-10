package com.aquabasilea.migrosapi.model.book;

import com.brugalibre.util.file.json.JsonUtil;

public record CancelCourse(String language, String bookingIdTac) {

    public static CancelCourse of(String bookingIdTac) {
        return new CancelCourse("de", bookingIdTac);
    }

    public String getJson() {
        return JsonUtil.createJsonFromObject(this);
    }
}
