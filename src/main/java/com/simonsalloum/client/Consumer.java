package com.simonsalloum.client;

import com.simonsalloum.service.QueueService;
import com.simonsalloum.service.QueueServiceRecord;
import com.simonsalloum.service.QueueServiceResponse;
import com.simonsalloum.service.QueueServiceResponseCode;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * A simple consumer used to asynchronously retrieve records
 * from any queue implementing the {@link QueueService} interface.
 * The consumer sends a delete request whenever the future is
 * completed and a record was found, triggering the queue to remove
 * the retrieved record.
 *
 * @author simon.salloum
 */
public class Consumer {

    public Consumer() {}

    /**
     * Retrieves one record from a queue and sends delete request upon retrieval
     * @param queue the queue to retrieve a record from
     * @return Future<QueueServiceResponse> a future with a response
     *         from the queue, including a status code and the record
     */
    public Future<QueueServiceResponse> consume(QueueService<QueueServiceRecord, QueueServiceResponse> queue) {
        return CompletableFuture.supplyAsync(queue::pull).thenApply(result -> {
            if (result.getResponseCode() == QueueServiceResponseCode.RECORD_FOUND)
                queue.delete(result.getQueueServiceRecord());
            return result;
        });
    }

    public Future<QueueServiceResponse> consumeFromFile(QueueService<QueueServiceRecord, QueueServiceResponse> queue) {
        return CompletableFuture.supplyAsync(queue::pull);
    }
}