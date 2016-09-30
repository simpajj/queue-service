package com.simonsalloum.service;

import com.simonsalloum.client.Consumer;
import com.simonsalloum.client.Producer;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class FileQueueTest {
    private static Producer<UUID, String> producer;
    private static Consumer consumer;
    private static FileQueueService fileQueueService;

    @Before
    public void setUp() throws IOException {
        producer = new Producer<>();
        consumer = new Consumer();
        fileQueueService = new FileQueueService();
    }

    // TODO: cleanup
    @Test
    public void testPushToFile() throws ExecutionException, InterruptedException {
        String hej = "lolul";
        String hej2 = "hej";
        producer.send(fileQueueService, UUID.randomUUID(), hej).get();
        producer.send(fileQueueService, UUID.randomUUID(), hej2).get();

        QueueServiceResponse response = fileQueueService.pull();
        System.out.println(response.getQueueServiceRecord());
        System.out.println(response.getQueueServiceRecord().getValue());
    }
}