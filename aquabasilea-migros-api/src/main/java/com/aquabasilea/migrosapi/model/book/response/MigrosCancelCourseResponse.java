package com.aquabasilea.migrosapi.model.book.response;

public class MigrosCancelCourseResponse {

    private String bookingIdTac;
    private int code;
    private String message;

    public String getBookingIdTac() {
        return bookingIdTac;
    }

    public void setBookingIdTac(String bookingIdTac) {
        this.bookingIdTac = bookingIdTac;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccessful(String expectedBookingIdTac) {
        return code == 0
                && message == null
                && (expectedBookingIdTac != null && expectedBookingIdTac.equals(bookingIdTac));
    }
}
