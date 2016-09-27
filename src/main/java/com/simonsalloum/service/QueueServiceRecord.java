package com.simonsalloum.service;

import java.util.UUID;

/**
 * An immutable record specific to the {@link InMemoryQueueService}
 * implementation of the {@link QueueService} interface.
 */
public class QueueServiceRecord<K, V> {
    private final UUID key;
    private final V value;

    /**
     * Upon construction, the record is assigned its own {@link UUID}
     * @param value the value, of type V, to be stored in the queue
     */
    public QueueServiceRecord(V value) {
        this.key = UUID.randomUUID();
        this.value = value;
    }

    public UUID getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[key=" + getKey() + ", " + "value=" + getValue() + "]";
    }
}