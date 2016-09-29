package com.simonsalloum.service;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A file based FIFO queue implementation of the {@link QueueService}
 * interface. The implementation uses a {@link FileChannel} to write
 * serialized {@link QueueServiceRecord}s to a file. The file location
 * is specified via a configuration file and read at construction.
 *
 * The object serialization and deserialization is handled by
 * {@link SerializationUtil}, which uses Java serialization, in turn
 * requiring that the {@link QueueServiceRecord#value} is serializable.
 *
 * The implementation is designed to be used with the non-blocking client
 * implementations {@link com.simonsalloum.client.Producer} and
 * {@link com.simonsalloum.client.Consumer}.
 *
 * @author simon.salloum
 **/

public class FileQueueService implements QueueService {

    private static final Logger LOGGER = Logger.getLogger(InMemoryQueueService.class.getName());
    private static final String SEPARATOR = System.getProperty("line.separator");
    private static String filePath;
    private static Properties props;

    public FileQueueService() throws IOException {
        loadProperties();
        filePath = props.getProperty("path");
        File file = new File(filePath);
        file.createNewFile();
    }

    @Override
    public synchronized QueueServiceResponse push(QueueServiceRecord record) {
        byte[] inputAsBytes;

        try {
            inputAsBytes = SerializationUtil.serialize(record.getValue());
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, e.toString());
            return new QueueServiceResponse(QueueServiceResponse.ResponseCode.RECORD_NOT_SERIALIZABLE);
        }

        ByteBuffer buffer = ByteBuffer.wrap(inputAsBytes);

        return writeToLogFile(buffer, record);
    }

    @Override
    public QueueServiceResponse pull() {
        return null;
    }

    @Override
    public void delete(QueueServiceRecord record) {}

    private QueueServiceResponse writeToLogFile(ByteBuffer buffer, QueueServiceRecord record) {
        try {
            FileOutputStream fos = new FileOutputStream(filePath, true);
            FileChannel fileChannel = fos.getChannel();
            ByteBuffer separatorBuffer = ByteBuffer.wrap(SEPARATOR.getBytes());

            fileChannel.write(separatorBuffer);
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
