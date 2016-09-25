package com.simonsalloum.service;

public class FileQueueService implements QueueService<QueueServiceRecord, QueueServiceResponse> {

    private final String path;

    public FileQueueService(String path) {
        this.path = path;
    }

    // TODO: inject path into constructor from config and remove this constructor
    public FileQueueService() {
        this.path = "/Users.simon.salloum/Desktop";
    }

    @Override
    public QueueServiceResponse push(QueueServiceRecord record) {
        return new QueueServiceResponse(QueueServiceResponseCode.RECORD_PRODUCED, record);
    }

    @Override
    public QueueServiceResponse pull() {
        return null;
    }

    @Override
    public void delete(QueueServiceRecord record) {

    }
}
