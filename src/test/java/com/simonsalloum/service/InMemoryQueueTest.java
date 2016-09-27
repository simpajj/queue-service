package com.simonsalloum.service;

import com.google.common.testing.FakeTicker;
import com.simonsalloum.client.Consumer;
import com.simonsalloum.client.Producer;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InMemoryQueueTest {

    private static final UUID KEY = UUID.randomUUID();
    private static final String VALUE = "hi";
    private static final Producer<UUID, String> producerOne = new Producer<>();
    private static final Producer<UUID, String> producerTwo = new Producer<>();
    private static final Consumer consumer = new Consumer();
    private static InMemoryQueueService inMemoryQueueService;

    @Before
    public void setUp() {
        inMemoryQueueService = new InMemoryQueueService();
    }

    @Test
    public void testPushOneRecord() throws ExecutionException, InterruptedException {
        Future<QueueServiceResponse> response = producerOne.send(inMemoryQueueService, KEY, VALUE);

        assertEquals(QueueServiceResponse.ResponseCode.RECORD_PRODUCED, response.get().getResponseCode());
        assertEquals(1, inMemoryQueueService.size());
    }

    @Test
    public void testPushMultipleRecordsOneProducer() throws ExecutionException, InterruptedException {
        ArrayList<Future<QueueServiceResponse>> responses = new ArrayList<>();
        responses.add(producerOne.send(inMemoryQueueService, KEY, VALUE));
        responses.add(producerTwo.send(inMemoryQueueService, KEY, VALUE));

        for (Future<QueueServiceResponse> response : responses) {
            assertEquals(QueueServiceResponse.ResponseCode.RECORD_PRODUCED, response.get().getResponseCode());
        }
        assertEquals(2, inMemoryQueueService.size());
    }

    @Test
    public void testPushMultipleRecordsMultipleProducers() throws ExecutionException, InterruptedException {
        ArrayList<Future<QueueServiceResponse>> responses = new ArrayList<>();
        for (int i = 0; i < 10; ++i) {
            responses.add(producerOne.send(inMemoryQueueService, KEY, VALUE));
            responses.add(producerTwo.send(inMemoryQueueService, KEY, VALUE));
        }

        for(Future<QueueServiceResponse> response : responses) {
            assertEquals(QueueServiceResponse.ResponseCode.RECORD_PRODUCED, response.get().getResponseCode());
        }
        assertEquals(20, inMemoryQueueService.size());
    }

    @Test
    public void testPushNullRecord() {
        QueueServiceResponse response = inMemoryQueueService.push(null);
        assertEquals(QueueServiceResponse.ResponseCode.RECORD_WAS_NULL, response.getResponseCode());
    }

    @Test
    public void testPushNullValue() throws ExecutionException, InterruptedException {
        Future<QueueServiceResponse> response = producerOne.send(inMemoryQueueService, KEY, null);
        assertEquals(QueueServiceResponse.ResponseCode.RECORD_PRODUCED, response.get().getResponseCode());
    }

    @Test
    public void testPushNullKey() throws ExecutionException, InterruptedException {
        Future<QueueServiceResponse> response = producerOne.send(inMemoryQueueService, null, VALUE);
        assertEquals(QueueServiceResponse.ResponseCode.RECORD_PRODUCED, response.get().getResponseCode());
    }

    @Test
    public void testPushMultipleRecordsNullKey() {
        QueueServiceRecord record = new QueueServiceRecord<>(null, VALUE);
        inMemoryQueueService.push(record);
        inMemoryQueueService.push(record);

        assertEquals(2, inMemoryQueueService.size());
    }

    @Test
    public void testPushMultipleQueueInstances() throws ExecutionException, InterruptedException {
        InMemoryQueueService queueService2 = new InMemoryQueueService();
        Future<QueueServiceResponse> response = producerOne.send(inMemoryQueueService, KEY, VALUE);
        response.get();
        assertEquals(1, queueService2.size());
    }

    @Test
    public void testPullOneRecord() throws ExecutionException, InterruptedException {
        InMemoryQueueService queueServiceMock = mock(InMemoryQueueService.class);
        QueueServiceRecord queueServiceRecord = new QueueServiceRecord<>(KEY, VALUE);
        QueueServiceResponse response = new QueueServiceResponse(QueueServiceResponse.ResponseCode.RECORD_FOUND, queueServiceRecord);

        when(queueServiceMock.pull()).thenReturn(response);
        String value = (String) consumer.consume(queueServiceMock).get().getQueueServiceRecord().getValue();
        assertNotNull(value);
        assertEquals(queueServiceRecord.getValue(), value);
    }

    @Test
    public void testPullOneRecordEmptyQueue() throws ExecutionException, InterruptedException {
        Future<QueueServiceResponse> response = consumer.consume(inMemoryQueueService);
        assertEquals(QueueServiceResponse.ResponseCode.QUEUE_EMPTY, response.get().getResponseCode());
    }

    @Test
    public void testPullAndDeleteOneRecord() throws ExecutionException, InterruptedException {
        QueueServiceRecord queueServiceRecord = new QueueServiceRecord<>(KEY, VALUE);

        inMemoryQueueService.push(queueServiceRecord);
        assertEquals(1, inMemoryQueueService.size());

        QueueServiceResponse res = inMemoryQueueService.pull();
        assertEquals(0, inMemoryQueueService.size());
        assertEquals(1, inMemoryQueueService.consumedMessages());

        inMemoryQueueService.delete(res.getQueueServiceRecord());
        assertEquals(0, inMemoryQueueService.consumedMessages());
    }

    @Test
    public void testExpireConsumedRecord() {
        FakeTicker ticker = new FakeTicker();
        InMemoryQueueService queueService = new InMemoryQueueService(2, TimeUnit.SECONDS, ticker);
        QueueServiceRecord queueServiceRecord = new QueueServiceRecord<>(KEY, VALUE);

        queueService.push(queueServiceRecord);
        assertEquals(1, queueService.size());

        queueService.pull();
        ticker.advance(1, TimeUnit.SECONDS);
        assertEquals(0, queueService.size());
        assertEquals(1, queueService.consumedMessages());

        ticker.advance(2, TimeUnit.SECONDS);
        assertEquals(0, queueService.consumedMessages());
    }

    @Test
    public void testPullRecordMultipleValuesSameKey() {
        QueueServiceRecord record = new QueueServiceRecord<>(null, VALUE);
        inMemoryQueueService.push(record);
        inMemoryQueueService.push(record);

        inMemoryQueueService.pull();
        assertEquals(1, inMemoryQueueService.size());
    }
}
