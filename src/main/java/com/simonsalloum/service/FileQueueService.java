package com.simonsalloum.service;

public class FileQueueService implements QueueService<QueueServiceRecord, QueueServiceResponse> {
    @Override
    public QueueServiceResponse push(QueueServiceRecord record) {
        return null;
    }

    @Override
    public QueueServiceResponse pull() {
        return null;
    }

    @Override
    public void delete(QueueServiceRecord record) {

    }
}
