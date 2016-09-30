package com.simonsalloum.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A file based FIFO queue implementation of the {@link QueueService}
 * interface. The implementation uses a {@link FileChannel} to write
 * serialized {@link QueueServiceRecord}s to a file. The file location
 * is specified via a configuration file and read at construction.
 *
 * The implementation is designed to be used with the non-blocking client
 * implementations {@link com.simonsalloum.client.Producer} and
 * {@link com.simonsalloum.client.Consumer}.
 *
 * @author simon.salloum
 **/

public class FileQueueService implements QueueService {

    private static final Logger LOGGER = Logger.getLogger(InMemoryQueueService.class.getName());
    private static Properties props;
    private static File file;
    private static ObjectMapper mapper;
    private static JsonParser jsonParser;

    public FileQueueService() throws IOException {
        loadProperties();

        String filePath = props.getProperty("path");
        file = new File(filePath);
        file.createNewFile();
        mapper = new ObjectMapper();
        JsonFactory jsonFactory = new JsonFactory();
        jsonParser = jsonFactory.createParser(file);
    }

    @Override
    public synchronized QueueServiceResponse push(QueueServiceRecord record) {
        return writeToLogFile(record);
    }

    @Override
    public synchronized QueueServiceResponse pull() {
        try {
            MappingIterator<Object> values = mapper.readValues(jsonParser, Object.class);
            if (values.hasNext()) {
                Object value = values.next();
                QueueServiceRecord record = new QueueServiceRecord<>(null, value);
                delete(record);
                return new QueueServiceResponse(QueueServiceResponse.ResponseCode.RECORD_FOUND, new QueueServiceRecord<>(null, record));
            } else {
                return new QueueServiceResponse(QueueServiceResponse.ResponseCode.COULD_NOT_DESERIALIZE_OBJECT);
            }
        } catch (IOException e) {
            return new QueueServiceResponse(QueueServiceResponse.ResponseCode.COULD_NOT_DESERIALIZE_OBJECT);
        }
    }

    @Override
    public synchronized void delete(QueueServiceRecord record) {
        // TODO: implement
    }

    private QueueServiceResponse writeToLogFile(QueueServiceRecord record) {
        try {
            final byte[] bytesToWrite = mapper.writeValueAsBytes(record.getValue());
            Files.write(file.toPath(), bytesToWrite, StandardOpenOption.APPEND);
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
