package ru.nuthatch.filteringutility.fio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class FileProducer implements Runnable {

    private final String INTEGER_REGEX = "-?[0-9]*";

    private final BlockingQueue<String> integersQueue;
    private final BlockingQueue<String> floatsQueue;
    private final BlockingQueue<String> stringsQueue;
    private final File file;

    public FileProducer(BlockingQueue<String> integersQueue,
                        BlockingQueue<String> floatsQueue,
                        BlockingQueue<String> stringsQueue,
                        File file) {
        this.integersQueue = integersQueue;
        this.floatsQueue = floatsQueue;
        this.stringsQueue = stringsQueue;
        this.file = file;
    }

    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                destinationQueue(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void destinationQueue(String value) {
        try {
            Float f = Float.parseFloat(value);
            /*
            Из-за ограничений max/min значения типа Integer проверяем соответствие
            регулярному выражению взамен Integer.parseInt()
             */
            if (value.matches(INTEGER_REGEX)) {
                integersQueue.offer(value);
            } else {
                floatsQueue.offer(value);
            }
        } catch (NumberFormatException _) {
            stringsQueue.offer(value);
        }
    }
}
