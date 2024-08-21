package ru.nuthatch.filteringutility.fio;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class FileConsumer implements Runnable {

    private final BlockingQueue<String> queue;
    private final File file;
    private final Boolean appendIfFileExist;

    public FileConsumer(BlockingQueue<String> queue,
                        File file,
                        Boolean appendIfFileExist) {
        this.queue = queue;
        this.file = file;
        this.appendIfFileExist = appendIfFileExist;
    }

    @Override
    public void run() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, appendIfFileExist))) {
            String line;
            while ((line = queue.poll(100, TimeUnit.MILLISECONDS)) != null) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
