package com.simonsalloum.service;

import com.google.common.base.Ticker;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An in-memory queue service implementing the {@link QueueService}.
 * This queue service uses an unbounded {@link ConcurrentLinkedQueue}
 * to store records in memory for consumption, as well as a {@link Cache}
 * for intermediate storage of retrieved records. Each entry in the cache is
 * automatically evicted after a configurable amount of time.
 *
 * The implementation is designed to be non-blocking, by using concurrent
 * data structures. The non-blocking nature is further supported by an
 * asynchronous client library designed to be used with the {@link QueueService}
 * interface. See {@link com.simonsalloum.client.Producer} and
 * {@link com.simonsalloum.client.Consumer} for more information.
 *
 * @author simon.salloum
 */

class InMemoryQueueService implements QueueService {

    private static final Logger LOGGER = Logger.getLogger(InMemoryQueueService.class.getName());
    private static ConcurrentLinkedQueue<QueueServiceRecord> queue;
    private static Cache<QueueServiceRecord.Key, QueueServiceRecord> consumedMessages;

    /**
     * Used to override the default cache eviction time of 30s.
     * @param evictionTime the maximum time that an entry can be in the consumedMessages cache
     *                     before being evicted
     * @param timeUnit the {@link TimeUnit} of the evictionTime parameter
     */
    InMemoryQueueService(int evictionTime, TimeUnit timeUnit, Ticker ticker) {
        queue = new ConcurrentLinkedQueue<>();
        consumedMessages = CacheBuilder.newBuilder().ticker(ticker).expireAfterWrite(evictionTime, timeUnit).build();
    }

    /**
     * Default constructor which sets the cache eviction time to 30s.
     */
    InMemoryQueueService() {
        queue = new ConcurrentLinkedQueue<>();
        consumedMessages = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).build();
    }

    @Override
    public QueueServiceResponse push(QueueServiceRecord queueServiceRecord) {
        if (queueServiceRecord != null) {
            if (queueServiceRecord.getValue() == null) LOGGER.log(Level.WARNING, "Storing a record with a null value");
            try {
                queue.add(queueServiceRecord);
                return new QueueServiceResponse(QueueServiceResponse.ResponseCode.RECORD_PRODUCED);
            } catch (IllegalStateException e) {
                LOGGER.log(Level.WARNING, "Trying to push a record to a full queue");
                return new QueueServiceResponse(QueueServiceResponse.ResponseCode.QUEUE_FULL);
            }
        }
        else return new QueueServiceResponse(QueueServiceResponse.ResponseCode.RECORD_WAS_NULL);
    }

    @Override
    public QueueServiceResponse pull() {
        try {
            QueueServiceRecord queueServiceRecord = queue.remove();
            consumedMessages.put(queueServiceRecord.getKey(), queueServiceRecord);

            return new QueueServiceResponse(QueueServiceResponse.ResponseCode.RECORD_FOUND, queueServiceRecord);
        } catch (NoSuchElementException e) {
            LOGGER.log(Level.WARNING, "Trying to pull a record from an empty queue");
            return new QueueServiceResponse(QueueServiceResponse.ResponseCode.QUEUE_EMPTY);
        }
    }

    @Override
    public void delete(QueueServiceRecord queueServiceRecord) {
        LOGGER.log(Level.FINE, "Removing queueServiceRecord: " + queueServiceRecord.getKey());
        consumedMessages.invalidate(queueServiceRecord.getKey());
    }

    int size() {
        return queue.size();
    }

    long consumedMessages() {
        consumedMessages.cleanUp();
        return consumedMessages.size();
    }
}