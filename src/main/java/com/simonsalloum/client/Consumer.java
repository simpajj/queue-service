package com.simonsalloum.client;

import com.simonsalloum.service.QueueService;
import com.simonsalloum.service.QueueServiceRecord;
import com.simonsalloum.service.QueueServiceResponse;
import com.simonsalloum.service.QueueServiceResponseCode;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * A simple Consumer used to asynchronously retrieve records
 * from any class implementing the {@link QueueService} interface.
 * The consumer sends a delete request whenever the future is
 * completed, triggering the queue to remove the retrieved record.
 *
 * @author simon.salloum
 */
public class Consumer {

    public Consumer() {}

    /**
     * Retrieves one record from a queue.
     * @param queueService the queue to retrieve a record from
     * @return Future<QueueServiceResponse> a future with a response
     *         from the queue, including a status code and the record
     */
    public Future<QueueServiceResponse> consume(QueueService<QueueServiceRecord, QueueServiceResponse> queueService) {
        return CompletableFuture.supplyAsync(queueService::pull).thenApply(result -> {
            if (result.getResponseCode() == QueueServiceResponseCode.RECORD_FOUND)
                queueService.delete(result.getQueueServiceRecord());
            return result;
        });
    }
}