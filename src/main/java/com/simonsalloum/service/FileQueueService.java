package com.simonsalloum.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileQueueService implements QueueService<QueueServiceRecord, QueueServiceResponse> {

    private static String path;
    private static String fileName;

    public FileQueueService(String filePath) {
        path = filePath;
    }

    // TODO: inject path into constructor from config and remove this constructor
    public FileQueueService() {
        path = "/Users/simon.salloum/Desktop/";
        fileName = "hej.txt";
    }

    // TODO: start out with synchronization, see if it can be done better later

    @Override
    public synchronized QueueServiceResponse push(QueueServiceRecord record) {
        String input = "hej";
        byte[] inputBytes = input.getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(inputBytes);
        String filePath = path + fileName;
        File file = new File(filePath);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            FileChannel fileChannel = fos.getChannel();
            int noOfBytesWritten = fileChannel.write(buffer);
            fileChannel.close();
            fos.close();
        } catch (FileNotFoundException fnf) {
            System.out.println("nädu");
        } catch (IOException ioe) {
            System.out.println("nä");
        }

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
