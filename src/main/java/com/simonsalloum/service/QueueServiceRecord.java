package com.simonsalloum.service;

import java.util.UUID;

/**
 * A record specific to the InMemoryQueueService implementation.
 */
public class QueueServiceRecord {
    private final UUID uuid;
    private final String value;

    /**
     * Upon initialization, the record is assigned its own {@link UUID}
     * @param value the value to be stored in the queue
     */
    public QueueServiceRecord(String value) {
        uuid = UUID.randomUUID();
        this.value = value;
    }

    /**
     * Get the {@link UUID} of this record
     * @return the {@link UUID} of the record
     */
    public UUID getId() {
        return uuid;
    }

    /**
     * Get the value of the record
     * @return the value of the record as a {@link String}
     */
    public String getValue() {
        return value;
    }
}
