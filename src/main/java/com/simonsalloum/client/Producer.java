package com.simonsalloum.client;

import com.simonsalloum.service.QueueService;
import com.simonsalloum.service.QueueServiceRecord;
import com.simonsalloum.service.QueueServiceResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * A simple Producer used to asynchronously send records
 * to and receive responses from any class implementing
 * the QueueService interface.
 *
 * @author simon.salloum
 */
public class Producer {

    public Producer() {}

    /**
     * Sends one message to a queue.
     * @param queueService the receiving queue
     * @param message the message to be sent to the queue
     * @return Future<QueueServiceResponse> a future with a
     *         response from the queue, including a status code
     */
    public Future<QueueServiceResponse> send(QueueService<QueueServiceRecord, QueueServiceResponse> queueService, String message) {
        return CompletableFuture.supplyAsync(() -> queueService.push(new QueueServiceRecord(message)));
    }
}