package com.simonsalloum.service;

/**
 * A QueueService interface with records of type {@link QueueServiceRecord}
 * and responses of type {@link QueueServiceResponse}
 *
 * @author simon.salloum
 */

public interface QueueService {
    /**
     * Pushes a record of an arbitrary type onto a queue
     * @param record the record of type T to push onto the queue
     * @return a response of type R
     */
    QueueServiceResponse push(QueueServiceRecord record);

    /**
     * Retrieves a single record from a queue
     * @return QueueServiceResponse including the record that was pulled
     */
    QueueServiceResponse pull();

    /**
     * Deletes a record from the queue that was received by a pull
     * @param record the record of type T to delete from the queue
     */
    void delete(QueueServiceRecord record);

}