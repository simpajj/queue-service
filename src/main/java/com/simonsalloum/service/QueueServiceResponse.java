package com.simonsalloum.service;


import javax.annotation.Nullable;

/**
 * An immutable response specific to the {@link InMemoryQueueService}
 * implementation of the {@link QueueService} interface.
 */
public class QueueServiceResponse {
    public enum ResponseCode {
        RECORD_PRODUCED,
        RECORD_WAS_NULL,
        RECORD_FOUND,
        RECORD_NOT_FOUND,
        QUEUE_FULL;

        @Override
        public String toString() {
            return this.getClass().getSimpleName() + "[response=" + this.name() + "]";
        }
    }

    private final ResponseCode responseCode;
    @Nullable private final QueueServiceRecord queueServiceRecord;

    /**
     * Creates a QueueServiceResponse with the given {@link ResponseCode}
     * and the record set to null
     * @param responseCode the {@link ResponseCode} to be used in this response
     */
    QueueServiceResponse(ResponseCode responseCode) {
        this.responseCode = responseCode;
        queueServiceRecord = null;
    }

    /**
     * Creates a QueueServiceResponse with the given {@link ResponseCode}
     * and the given {@link QueueServiceRecord}
     * @param responseCode the {@link ResponseCode} to be used in the response
     * @param record the {@link QueueServiceRecord} to be used in the response
     */
    QueueServiceResponse(ResponseCode responseCode, @Nullable QueueServiceRecord record) {
        this.responseCode = responseCode;
        this.queueServiceRecord = record;
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }

    /**
     * @return the {@link QueueServiceRecord} or null if the record does not exist
     */
    @Nullable
    public QueueServiceRecord getQueueServiceRecord() {
        if (queueServiceRecord != null)
            return queueServiceRecord;
        else
            return null;
    }

    @Override
    public String toString() {
        if (queueServiceRecord != null)
            return this.getClass().getSimpleName() + "[" +
                    "responseCode=" + getResponseCode().toString() + ", " +
                    "queueServiceRecord=" + getQueueServiceRecord().toString() +
                    "]";
        else
            return this.getClass().getSimpleName() + "[" +
                    "responseCode=" + getResponseCode().toString() +
                    "]";
    }
}
