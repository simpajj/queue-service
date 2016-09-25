package com.simonsalloum.service;

/***
 *
 ***/
public enum QueueServiceResponseCode {
    RECORD_PRODUCED(0),
    RECORD_WAS_NULL(100),
    RECORD_FOUND(200),
    RECORD_NOT_FOUND(300);

    private final int responseCode;

    QueueServiceResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
}
