package com.simonsalloum.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * An immutable record specific to the {@link InMemoryQueueService}
 * implementation of the {@link QueueService} interface.
 */
public class QueueServiceRecord<K, V> implements Serializable {

    /**
     * The key, of type T, of the QueueServiceRecord
     * @param <T>
     */
    static class Key<T> implements Serializable {
        private final T key;

        Key(T key) {
            this.key = key;
        }

        public T getRawKey() {
            return key;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key<?> key1 = (Key<?>) o;
            return Objects.equal(key, key1.key);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(key);
        }
    }

    private final Key<K> key;
    @Nullable private final V value;

    /**
     * @param key the key to be stored in the queue, of type K or null if no key is specified
     * @param value the value, of type V, to be stored in the queue
     */
    @JsonCreator
    public QueueServiceRecord(@JsonProperty("key") K key, @Nullable @JsonProperty("value") V value) {
        this.key = new Key<>(key);
        this.value = value;
    }

    public Key<K> getKey() {
        return key;
    }

    @Nullable
    public V getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[key=" + getKey() + ", " + "value=" + getValue() + "]";
    }
}