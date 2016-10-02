package com.simonsalloum.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.*;
import com.google.common.io.Files;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A file based FIFO queue implementation of the {@link QueueService}
 * interface. The implementation uses {@link Files} to append serialized
 * {@link QueueServiceRecord}s to a file. The serialization is handled
 * by {@link ObjectMapper}, and thus requires that serialized objects are
 * deserializable, e.g. via the {@link com.fasterxml.jackson.annotation.JsonCreator}
 * and {@link com.fasterxml.jackson.annotation.JsonProperty} annotations.
 *
 * The queue file location is specified via a configuration file and read
 * at queue construction. It uses a {@link Cache} as an intermediate in-memory
 * storage for consumed messages. Consumed messages are either evicted by the
 * consumer or forcibly re-added to the queue file if expired.
 *
 * The implementation is designed to be used with the non-blocking client
 * implementations {@link com.simonsalloum.client.Producer} and
 * {@link com.simonsalloum.client.Consumer}.
 *
 * @author simon.salloum
 **/

class FileQueueService implements QueueService {

    private static final Logger LOGGER = Logger.getLogger(InMemoryQueueService.class.getName());
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static Properties props;
    private static File file;
    private static Cache<QueueServiceRecord.Key, QueueServiceRecord> consumedMessages;

    FileQueueService() throws IOException {
        loadProperties();

        String filePath = props.getProperty("path");
        file = new File(filePath);
        try {
            file.createNewFile();
        } catch (IOException | SecurityException e) {
            LOGGER.log(Level.SEVERE, e.toString());
        }
        RemovalListener<QueueServiceRecord.Key, QueueServiceRecord> removalListener = notification -> {
            if (notification.getCause() == RemovalCause.EXPIRED) {
                try {
                    Files.append(MAPPER.writeValueAsString(notification.getValue()) + System.lineSeparator(), file, Charset.defaultCharset());
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, e.toString());
                }
            }
        };
        consumedMessages = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).removalListener(removalListener).build();
    }

    /**
     * Appends a single record into the file based queue
     * @param record the record to push onto the queue
     * @return a {@link QueueServiceResponse}
     */
    @Override
    public synchronized QueueServiceResponse push(QueueServiceRecord record) {
        QueueServiceResponse response = writeToLogFile(record);
        notify();
        return response;
    }

    /**
     * Removes and returns the first message in the queue
     * @return A {@link QueueServiceResponse} that may include a {@link QueueServiceRecord}
     */
    @Override
    public synchronized QueueServiceResponse pull() {
        QueueServiceResponse response = readFromLogFile();
        notify();
        return response;
    }

    /**
     * Deletes a received record from the intermediate storage
     * @param record the record of type {@link QueueServiceRecord} to delete from the intermediate storage
     */
    @Override
    public synchronized void delete(QueueServiceRecord record) {
        LOGGER.log(Level.FINE, "Removing queueServiceRecord: " + record.getKey());
        consumedMessages.invalidate(record.getKey());
    }

    private QueueServiceResponse readFromLogFile() {
        try {
            File tempFile = new File(file + ".tmp");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            BufferedWriter tempFileWriter = new BufferedWriter(new FileWriter(tempFile));
            String firstLine = reader.readLine();
            String currentLine;
            while((currentLine = reader.readLine()) != null) {
                tempFileWriter.write(currentLine + System.lineSeparator());
            }

            tempFileWriter.close();
            reader.close();
            tempFile.renameTo(file);
            if (firstLine != null) {
                QueueServiceRecord record = MAPPER.readValue(firstLine, QueueServiceRecord.class);
                consumedMessages.put(record.getKey(), record);
                return new QueueServiceResponse(QueueServiceResponse.ResponseCode.RECORD_FOUND, record);
            }
            else return new QueueServiceResponse(QueueServiceResponse.ResponseCode.COULD_NOT_DESERIALIZE_RECORD);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, e.toString());
            return new QueueServiceResponse(QueueServiceResponse.ResponseCode.COULD_NOT_DESERIALIZE_RECORD);
        }
    }

    private QueueServiceResponse writeToLogFile(QueueServiceRecord record) {
        try {
            Files.append(MAPPER.writeValueAsString(record) + System.lineSeparator(), file, Charset.defaultCharset());
            return new QueueServiceResponse(QueueServiceResponse.ResponseCode.RECORD_PRODUCED, record);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, e.toString());
            return new QueueServiceResponse(QueueServiceResponse.ResponseCode.COULD_NOT_WRITE_FILE, record);
        }
    }

    private void loadProperties() throws IOException {
        props = new Properties();
        InputStream in = new FileInputStream("config/config.properties");
        props.load(in);
        in.close();
    }
}