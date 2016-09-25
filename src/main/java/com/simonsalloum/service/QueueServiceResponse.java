package com.simonsalloum.service;


import javax.annotation.Nullable;

/**
 * A response specific to the {@link com.simonsalloum.service.InMemoryQueueService}
 * implementation of the {@link com.simonsalloum.service.QueueService} interface.
 */
public class QueueServiceResponse {
    private final QueueServiceResponseCode responseCode;
    @Nullable private final QueueServiceRecord queueServiceRecord;

    /**
     * Creates a QueueServiceResponse with the given {@link QueueServiceResponseCode}
     * and the record set to null
     * @param responseCode the {@link QueueServiceResponseCode} to be used in this response
     */
    QueueServiceResponse(QueueServiceResponseCode responseCode) {
        this.responseCode = responseCode;
        queueServiceRecord = null;
    }

    /**
     * Creates a QueueServiceResponse with the given {@link QueueServiceResponseCode}
     * and the given {@link QueueServiceRecord}
     * @param responseCode the {@link QueueServiceResponseCode} to be used in the response
     * @param record the {@link QueueServiceRecord} to be used in the response
     */
    QueueServiceResponse(QueueServiceResponseCode responseCode, @Nullable QueueServiceRecord record) {
        this.responseCode = responseCode;
        this.queueServiceRecord = record;
    }

    /**
     * Returns the {@link QueueServiceResponseCode} of the response
     * @return the {@link QueueServiceResponseCode}
     */
    public QueueServiceResponseCode getResponseCode() {
        return responseCode;
    }

    /**
     * Returns the {@link QueueServiceRecord}
     * @return the {@link QueueServiceRecord}
     * @throws NullPointerException if the record does not exist
     */
    @Nullable
    public QueueServiceRecord getQueueServiceRecord() throws NullPointerException {
        if (queueServiceRecord != null)
            return queueServiceRecord;
        else
            throw new NullPointerException();
    }

    /**
     * Returns the String representation of a QueueServiceResponse
     * @return the QueueServiceResponse as a string
     */
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
