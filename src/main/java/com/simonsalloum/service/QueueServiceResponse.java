package com.simonsalloum.service;

/**
 * A response from any class implementing the {@link com.simonsalloum.service.QueueService}
 * interface. The response may or may not include a record, depending on the implementation
 * details of the QueueService.
 */

import javax.annotation.Nullable;

public class QueueServiceResponse {
    private final QueueServiceResponseCode responseCode;
    @Nullable private final QueueServiceRecord queueServiceRecord;

    QueueServiceResponse(QueueServiceResponseCode responseCode) {
        this.responseCode = responseCode;
        queueServiceRecord = null;
    }

    QueueServiceResponse(QueueServiceResponseCode responseCode, @Nullable QueueServiceRecord record) {
        this.responseCode = responseCode;
        this.queueServiceRecord = record;
    }

    public QueueServiceResponseCode getResponseCode() {
        return responseCode;
    }

    @Nullable
    public QueueServiceRecord getQueueServiceRecord() {
        return queueServiceRecord;
    }
}
