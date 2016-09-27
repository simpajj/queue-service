package com.simonsalloum.service;

import com.simonsalloum.client.Consumer;
import com.simonsalloum.client.Producer;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FileQueueTest {
    private static Producer<UUID, String> producer;
    private static Consumer consumer;
    private static FileQueueService fileQueueService;
    private static final String MESSAGE = "hi";

    @Before
    public void setUp() {
        producer = new Producer<UUID, String>();
        consumer = new Consumer();
        fileQueueService = new FileQueueService();
    }

    @Test
    public void testPushToFile() throws ExecutionException, InterruptedException {
        Future<QueueServiceResponse> response = producer.send(fileQueueService, UUID.randomUUID(), MESSAGE);
        System.out.println(response.get().toString());
    }
}
