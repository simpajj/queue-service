package com.simonsalloum.service;

import com.simonsalloum.client.Consumer;
import com.simonsalloum.client.Producer;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

public class FileQueueTest {
    private static final String MESSAGE = "hi";
    private static Producer<String, String> producer;
    private static Consumer consumer;
    private static FileQueueService fileQueueService;

    @Before
    public void setUp() throws IOException {
        producer = new Producer<>();
        consumer = new Consumer();
        fileQueueService = new FileQueueService();
    }

    @Test
    public void testPushToFile() throws ExecutionException, InterruptedException {
        fileQueueService.deleteFileContents();
        assertEquals(QueueServiceResponse.ResponseCode.RECORD_PRODUCED, producer.send(fileQueueService, null, MESSAGE).get().getResponseCode());
    }

    @Test
    public void testPullFromFile() throws ExecutionException, InterruptedException {
        fileQueueService.deleteFileContents();
        producer.send(fileQueueService, null, MESSAGE).get();
        assertEquals(MESSAGE, consumer.consume(fileQueueService).get().getQueueServiceRecord().getValue());
    }

    @Test
    public void testDeleteFromFile() throws ExecutionException, InterruptedException {
        fileQueueService.deleteFileContents();
        producer.send(fileQueueService, null, MESSAGE).get();
        assertEquals(MESSAGE, consumer.consume(fileQueueService).get().getQueueServiceRecord().getValue());
        assertEquals(0, fileQueueService.getFileSize());
    }

    @Test
    public void testAddToConsumedMessagesCache() throws ExecutionException, InterruptedException {
        fileQueueService.deleteFileContents();
        producer.send(fileQueueService, null, MESSAGE).get();
        fileQueueService.pull();
        assertEquals(1, fileQueueService.getConsumedMessagesSize());
    }

    @Test
    public void testRemoveFromConsumedMessagesCache() throws ExecutionException, InterruptedException {
        fileQueueService.deleteFileContents();
        producer.send(fileQueueService, null, MESSAGE).get();
        assertEquals(MESSAGE, consumer.consume(fileQueueService).get().getQueueServiceRecord().getValue());
        assertEquals(0, fileQueueService.getConsumedMessagesSize());
    }
}