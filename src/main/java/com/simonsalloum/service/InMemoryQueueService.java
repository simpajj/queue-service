package com.simonsalloum.service;

import com.google.common.base.Ticker;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An in-memory queue service implementing the
 * {@link com.simonsalloum.service.QueueService} interface for pushing and
 * pulling records to and from it. This queue service uses a
 * {@link ConcurrentLinkedQueue}, which is unbounded, to store records
 * in memory for consumption, as well as a {@link com.google.common.cache.Cache}
 * for intermediate storage of retrieved records. Each entry in the cache is
 * automatically evicted after a configurable amount of time.
 *
 * The implementation is designed to be non-blocking and to not require
 * object level locks. This is further supported by the asynchronous
 * implementations of {@link com.simonsalloum.client.Producer} and
 * {@link com.simonsalloum.client.Consumer} respectively.
 *
 * The queue is designed to accept records of type
 * {@link com.simonsalloum.service.QueueServiceRecord} and to respond
 * with {@link com.simonsalloum.service.QueueServiceResponse}.
 *
 * @author simon.salloum
 */

// TODO: make the class generic and push out the record/response details to the clients
// TODO: look through javadocs of all classes to see if they are in-sync with generics changes
class InMemoryQueueService implements QueueService<QueueServiceRecord, QueueServiceResponse> {

    private static Logger LOGGER = Logger.getLogger(InMemoryQueueService.class.getName());
    private static Cache<UUID, QueueServiceRecord> consumedMessages;
    private static ConcurrentLinkedQueue<QueueServiceRecord> queue;

    /**
     * Used to override the default cache eviction time of 300s.
     * @param evictionTime the maximum time, given in seconds, that an
     *                       entry can be in the consumedMessages cache
     *                       before being evicted.
     * @param timeUnit the time unit of the eviction time
     */
    InMemoryQueueService(int evictionTime, TimeUnit timeUnit, Ticker ticker) {
        queue = new ConcurrentLinkedQueue<>();
        consumedMessages = CacheBuilder.newBuilder().ticker(ticker).expireAfterWrite(evictionTime, timeUnit).build();
    }

    /**
     * Default constructor which sets the cache eviction time to 300s.
     */
    InMemoryQueueService() {
        queue = new ConcurrentLinkedQueue<>();
        consumedMessages = CacheBuilder.newBuilder().expireAfterWrite(300, TimeUnit.SECONDS).build();
    }

    @Override
    public QueueServiceResponse push(QueueServiceRecord queueServiceRecord) {
        if (queueServiceRecord.getValue() != null) {
            queue.add(queueServiceRecord);
            return new QueueServiceResponse(QueueServiceResponseCode.RECORD_PRODUCED);
        }
        else return new QueueServiceResponse(QueueServiceResponseCode.RECORD_WAS_NULL);
    }

    @Override
    public QueueServiceResponse pull() {
        QueueServiceResponse response;
        try {
            QueueServiceRecord queueServiceRecord = queue.remove();
            consumedMessages.put(queueServiceRecord.getId(), queueServiceRecord);

            response = new QueueServiceResponse(QueueServiceResponseCode.RECORD_FOUND, queueServiceRecord);
            return response;
        } catch (NoSuchElementException e) {
            LOGGER.log(Level.WARNING, "Trying to pull a record from an empty queue");
            return new QueueServiceResponse(QueueServiceResponseCode.RECORD_NOT_FOUND);
        }
    }

    @Override
    public void delete(QueueServiceRecord queueServiceRecord) {
        LOGGER.log(Level.FINE, "Removing queueServiceRecord: " + queueServiceRecord.getId());
        consumedMessages.invalidate(queueServiceRecord.getId());
    }

    public int size() {
        return queue.size();
    }

    public long consumedMessages() {
        consumedMessages.cleanUp();
        return consumedMessages.size();
    }
}