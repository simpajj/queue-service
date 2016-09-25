package com.simonsalloum.service;

import com.google.common.testing.FakeTicker;
import com.simonsalloum.client.Consumer;
import com.simonsalloum.client.Producer;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InMemoryQueueTest {

    private static Producer producerOne;
    private static Producer producerTwo;
    private static Consumer consumer;
    private static InMemoryQueueService inMemoryQueueService;
    private static final String MESSAGE = "hi";

    @Before
    public void setUp() {
        producerOne = new Producer();
        producerTwo = new Producer();
        consumer = new Consumer();
        inMemoryQueueService = new InMemoryQueueService();
    }

    @Test
    public void testPushOneRecord() throws ExecutionException, InterruptedException {
        Future<QueueServiceResponse> response = producerOne.send(inMemoryQueueService, MESSAGE);

        assertEquals(QueueServiceResponseCode.RECORD_PRODUCED, response.get().getResponseCode());
        assertEquals(1, inMemoryQueueService.size());
    }

    @Test
    public void testPushMultipleRecordsOneProducer() throws ExecutionException, InterruptedException {
        ArrayList<Future<QueueServiceResponse>> responses = new ArrayList<>();
        responses.add(producerOne.send(inMemoryQueueService, MESSAGE));
        responses.add(producerTwo.send(inMemoryQueueService, MESSAGE));

        for (Future<QueueServiceResponse> response : responses) {
            assertEquals(QueueServiceResponseCode.RECORD_PRODUCED, response.get().getResponseCode());
        }
        assertEquals(2, inMemoryQueueService.size());
    }

    @Test
    public void testPushMultipleRecordsMultipleProducers() throws ExecutionException, InterruptedException {
        ArrayList<Future<QueueServiceResponse>> responses = new ArrayList<>();
        for (int i = 0; i < 2; ++i) {
            responses.add(producerOne.send(inMemoryQueueService, MESSAGE));
            responses.add(producerTwo.send(inMemoryQueueService, MESSAGE));
        }

        for(Future<QueueServiceResponse> response : responses) {
            assertEquals(QueueServiceResponseCode.RECORD_PRODUCED, response.get().getResponseCode());
        }
        assertEquals(4, inMemoryQueueService.size());
    }

    @Test
    public void testPushNullRecord() throws ExecutionException, InterruptedException {
        Future<QueueServiceResponse> response = producerOne.send(inMemoryQueueService, null);
        assertEquals(QueueServiceResponseCode.RECORD_WAS_NULL, response.get().getResponseCode());
    }

    @Test
    public void testPullOneRecord() throws ExecutionException, InterruptedException {
        InMemoryQueueService queueServiceMock = mock(InMemoryQueueService.class);
        QueueServiceRecord queueServiceRecord = new QueueServiceRecord(MESSAGE);
        QueueServiceResponse response = new QueueServiceResponse(QueueServiceResponseCode.RECORD_FOUND, queueServiceRecord);

        when(queueServiceMock.pull()).thenReturn(response);
        assertEquals(queueServiceRecord.getValue(), consumer.consume(queueServiceMock).get().getQueueServiceRecord().getValue());
    }

    @Test
    public void testPullOneRecordEmptyQueue() throws ExecutionException, InterruptedException {
        Future<QueueServiceResponse> response = consumer.consume(inMemoryQueueService);
        assertEquals(QueueServiceResponseCode.RECORD_NOT_FOUND, response.get().getResponseCode());
    }

    @Test
    public void testPullAndDeleteOneRecord() throws ExecutionException, InterruptedException {
        QueueServiceRecord queueServiceRecord = new QueueServiceRecord(MESSAGE);

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
        QueueServiceRecord queueServiceRecord = new QueueServiceRecord(MESSAGE);

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
    public void testPushMultipleQueueInstances() throws ExecutionException, InterruptedException {
        InMemoryQueueService queueService2 = new InMemoryQueueService();
        Future<QueueServiceResponse> response = producerOne.send(inMemoryQueueService, MESSAGE);
        response.get();
        assertEquals(1, queueService2.size());
    }
}
