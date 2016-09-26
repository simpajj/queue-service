package com.simonsalloum.service;

import java.util.UUID;

/**
 * A record specific to the {@link InMemoryQueueService}
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

    /**
     * Get the key, of type {@link UUID}, of this record
     * @return the key of the record
     */
    public UUID getId() {
        return key;
    }

    /**
     * Get the value, of type V, of the record
     * @return the value of the record
     */
    public V getValue() {
        return value;
    }

    /**
     * Returns the String representation of a QueueServiceRecord
     * @return the QueueServiceRecord as a string
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[key=" + key + ", " + "value=" + value + "]";
    }
}
