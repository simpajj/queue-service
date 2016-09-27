package com.simonsalloum.client;

import com.simonsalloum.service.QueueService;
import com.simonsalloum.service.QueueServiceRecord;
import com.simonsalloum.service.QueueServiceResponse;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * A simple producer used to asynchronously send records
 * to and receive responses from any queue implementing
 * the {@link QueueService} interface.
 *
 * @author simon.salloum
 */
public class Producer<K, V> {

    public Producer() {}

    /**
     * Sends one message to a queue.
     * @param queue the receiving queue
     * @param value the value to be sent to the queue
     * @return Future<QueueServiceResponse> a future with a
     *         response from the queue, including a status code
     */
    public Future<QueueServiceResponse> send(QueueService queue, K key, V value) {
        return CompletableFuture.supplyAsync(() -> queue.push(new QueueServiceRecord<>(key, value)));
    }
}