package com.github.svart63.model;

import java.time.LocalDateTime;

public class RequestMessage {
    private final String message;
    private final LocalDateTime time;

    public RequestMessage(String message) {
        this.message = message;
        this.time = LocalDateTime.now();
    }

    public String getMessage() {
        return this.message;
    }

    public String getTime() {
        return this.time.toString();
    }
}
