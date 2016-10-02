package com.simonsalloum.service;


import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * An immutable response specific to the {@link QueueService} interface.
 */
public class QueueServiceResponse implements Serializable {
    public enum ResponseCode implements Serializable {
        COULD_NOT_DESERIALIZE_RECORD,
        COULD_NOT_WRITE_FILE,
        QUEUE_EMPTY,
        QUEUE_FULL,
        RECORD_FOUND,
        RECORD_PRODUCED,
        RECORD_WAS_NULL;

        @Override
        public String toString() {
            return this.getClass().getSimpleName() + "[response=" + this.name() + "]";
        }
    }

    private final ResponseCode responseCode;
    @Nullable private final QueueServiceRecord queueServiceRecord;

    /**
     * Creates a QueueServiceResponse with the given {@link ResponseCode}
     * and the record set to null.
     * @param responseCode the {@link ResponseCode} to be used in the response
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
        return queueServiceRecord;
    }

    @Override
    public String toString() {
        if (getQueueServiceRecord() != null)
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
