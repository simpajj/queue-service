package com.simonsalloum.client;

import com.simonsalloum.service.QueueService;
import com.simonsalloum.service.QueueServiceRecord;
import com.simonsalloum.service.QueueServiceResponse;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * A simple producer used to asynchronously send records to and receive
 * responses from any queue implementing the {@link QueueService} interface.
 *
 * @param <K> the type of the key that the producer sends
 * @param <V> the type of the value that the producer sends
 *
 * @author simon.salloum
 */
public class Producer<K, V> {

    public Producer() {}

    /**
     * Sends one {@link QueueServiceRecord} to a queue.
     * @param queue the queue to send to
     * @param key the key of type K to send
     * @param value the value of type V to send
     * @return Future<QueueServiceResponse> a future with response from
     *         the queue, including a status code
     */
    public Future<QueueServiceResponse> send(QueueService queue, @Nullable K key, V value) {
        if (key == null) return CompletableFuture.supplyAsync(() -> queue.push(new QueueServiceRecord<>(value)));
        else return CompletableFuture.supplyAsync(() -> queue.push(new QueueServiceRecord<>(key, value)));
    }
}