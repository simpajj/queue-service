package com.simonsalloum.service;

import com.simonsalloum.client.Consumer;
import com.simonsalloum.client.Producer;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class FileQueueTest {
    private static Producer<UUID, byte[]> producer;
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
        TestClass hej = new TestClass("yo");
        TestClass hej2 = new TestClass("hej");
        try {
            byte[] bytes = SerializationUtil.serialize(hej);
            byte[] bytes2 = SerializationUtil.serialize(hej2);
            producer.send(fileQueueService, UUID.randomUUID(), bytes).get();
            producer.send(fileQueueService, UUID.randomUUID(), bytes2).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //fileQueueService.pull();
    }
}

class TestClass implements Serializable {
    private String testField;
    TestClass(String testField) {
        this.testField = testField;
    }
}
