package com.simonsalloum.service;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileQueueService implements QueueService {

    private static final Logger LOGGER = Logger.getLogger(InMemoryQueueService.class.getName());
    private static String filePath;
    private static Properties props;

    public FileQueueService() throws IOException {
        loadProperties();
        filePath = props.getProperty("path");
        System.out.println(filePath);
    }

    @Override
    public QueueServiceResponse push(QueueServiceRecord record) {
        byte[] inputAsBytes;

        try {
            inputAsBytes = SerializationUtil.serialize(record.getValue());
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, e.toString());
            return new QueueServiceResponse(QueueServiceResponse.ResponseCode.RECORD_NOT_SERIALIZABLE);
        }

        ByteBuffer buffer = ByteBuffer.wrap(inputAsBytes);
        File file = new File(filePath);

        try {
            file.createNewFile();
        } catch (IOException e) {
            return new QueueServiceResponse(QueueServiceResponse.ResponseCode.COULD_NOT_CREATE_FILE);
        }

        return writeToLogFile(buffer, record);
    }

    @Override
    public synchronized QueueServiceResponse pull() {
        return null;
    }

    @Override
    public synchronized void delete(QueueServiceRecord record) {}

    private QueueServiceResponse writeToLogFile(ByteBuffer buffer, QueueServiceRecord record) {
        try {
            FileOutputStream fos = new FileOutputStream(filePath, true);
            FileChannel fileChannel = fos.getChannel();
            fileChannel.write(buffer);
            fileChannel.close();
            fos.close();
            return new QueueServiceResponse(QueueServiceResponse.ResponseCode.RECORD_PRODUCED, record);
        } catch (FileNotFoundException fnf) {
            LOGGER.log(Level.WARNING, "No file found at path: " + filePath);
            return new QueueServiceResponse(QueueServiceResponse.ResponseCode.FILE_NOT_FOUND);
        } catch (IOException ioe) {
            LOGGER.log(Level.WARNING, "Could not write to file at path: " + filePath);
            return new QueueServiceResponse(QueueServiceResponse.ResponseCode.COULD_NOT_WRITE_FILE);
        }
    }

    private void loadProperties() throws IOException {
        props = new Properties();
        InputStream in = new FileInputStream("config/config.properties");
        props.load(in);
        in.close();
    }
}
