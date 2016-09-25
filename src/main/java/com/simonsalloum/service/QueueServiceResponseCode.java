package com.simonsalloum.service;

/***
 * An enum representing the different response codes of the a {@link QueueService}.
 ***/
public enum QueueServiceResponseCode {
    RECORD_PRODUCED(0),
    RECORD_WAS_NULL(100),
    RECORD_FOUND(200),
    RECORD_NOT_FOUND(300);

    private final int responseCode;

    /**
     *
     * @param responseCode the code associated with a specific response
     */
    QueueServiceResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    /**
     * Returns the code associated with a specific response
     * @return an int representing the code of the response
     */
    public int getCode() {
        return responseCode;
    }

    /**
     * Returns the String representation of a QueueServiceResponseCode
     * @return the QueueServiceResponseCode as a string
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[response=" + this.name() + ", " + "code=" + this.getCode() + "]";
    }
}
