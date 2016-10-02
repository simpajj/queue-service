package com.simonsalloum.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
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

class FileQueueService implements QueueService {

    private static final Logger LOGGER = Logger.getLogger(InMemoryQueueService.class.getName());
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static Properties props;
    private static File file;
    private static FileChannel channel;
    private static RandomAccessFile raf;
    private Writer writer;

    public FileQueueService() throws IOException {
        loadProperties();

        String filePath = props.getProperty("path");
        file = new File(filePath);
        file.createNewFile();
        raf = new RandomAccessFile(file, "rwd");
        channel = raf.getChannel();
    }

    @Override
    public synchronized QueueServiceResponse push(QueueServiceRecord record) {
        QueueServiceResponse response = writeToLogFile(record);
        notify();
        return response;
    }

    @Override
    public synchronized QueueServiceResponse pull() {

        return null;
    }

    @Override
    public synchronized void delete(QueueServiceRecord record) {
        try {
            raf.setLength(0);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Could not empty contents of file. Data duplication possible.");
        }
    }

    private QueueServiceResponse writeToLogFile(QueueServiceRecord record) {
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.append(MAPPER.writeValueAsString(record));
            writer.close();
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
