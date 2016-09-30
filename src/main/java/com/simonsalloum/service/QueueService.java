package com.simonsalloum.service;

/**
 * A QueueService interface with records of type {@link QueueServiceRecord}
 * and responses of type {@link QueueServiceResponse}
 *
 * @author simon.salloum
 */

public interface QueueService {
    /**
     * Pushes a record of type {@link QueueServiceRecord} onto a queue
     * @param record the record to push onto the queue
     * @return the response of type {@link QueueServiceResponse}
     */
    QueueServiceResponse push(QueueServiceRecord record);

    /**
     * Retrieves a single record from a queue. The record is wrapped in
     * a {@link QueueServiceResponse} and may be null. The record may
     * hold several values, depending on the implementation of the queue.
     * @return {@link QueueServiceResponse}, possibly including the record that was pulled
     */
    QueueServiceResponse pull();

    /**
     * Deletes a record from the queue
     * @param record the record of type {@link QueueServiceRecord} to delete from the queue
     */
    void delete(QueueServiceRecord record);

}