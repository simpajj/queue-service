package com.simonsalloum.service;

public class FileQueueService implements QueueService<QueueServiceRecord, QueueServiceResponse> {

    private static String path;
    private static String fileName;

    public FileQueueService(String filePath) {
        path = filePath;
    }

    // TODO: inject path into constructor from config and remove this constructor
    public FileQueueService() {
        path = "/Users.simon.salloum/Desktop";
    }

    // TODO: start out with synchronization, see if it can be done better later

    @Override
    public synchronized QueueServiceResponse push(QueueServiceRecord record) {
        return new QueueServiceResponse(QueueServiceResponse.ResponseCode.RECORD_PRODUCED, record);
    }

    @Override
    public synchronized QueueServiceResponse pull() {
        return null;
    }

    @Override
    public synchronized void delete(QueueServiceRecord record) {

    }
}
