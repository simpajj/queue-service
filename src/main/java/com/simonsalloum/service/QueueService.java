package com.simonsalloum.service;

/**
 * A QueueService interface with records of type T and responses of type R.
 * The interface is designed to be both record and response agnostic.
 *
 * @param <T> the type of records that the implementing queue accepts
 * @param <R> the type of response that the implementing queue returns
 *
 * @author simon.salloum
 */

public interface QueueService<T, R> {
    /**
     * Pushes a record of an arbitrary type onto a queue
     * @param record the record of type T to push onto the queue
     * @return a response of type R
     */
    R push(T record);

    /**
     * Retrieves a single record from a queue
     * @return QueueServiceResponse including the record that was pulled
     */
    R pull();

    /**
     * Deletes a record from the queue that was received by a pull
     * @param record the record of type T to delete from the queue
     */
    void delete(T record);

}