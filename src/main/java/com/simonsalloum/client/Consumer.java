package com.simonsalloum.client;

import com.simonsalloum.service.QueueService;
import com.simonsalloum.service.QueueServiceResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * A simple consumer used to asynchronously retrieve records from any queue
 * implementing the {@link QueueService} interface.
 *
 * @author simon.salloum
 */
public class Consumer {

    public Consumer() {}

    /**
     * Retrieves one record from a queue and sends a delete request upon a successful response
     * @param queue the queue to retrieve a record from
     * @return Future<QueueServiceResponse> a future with a response
     *         from the queue, including a status code and possibly the record
     */
    public Future<QueueServiceResponse> consume(QueueService queue) {
        return CompletableFuture.supplyAsync(queue::pull).thenApply(result -> {
            // TODO: if QueueService is of instance InMemoryQueueService, do the delete, else just return the result - update javadoc accordingly
            if (result.getResponseCode() == QueueServiceResponse.ResponseCode.RECORD_FOUND)
                queue.delete(result.getQueueServiceRecord());
            return result;
        });
    }
}