package ru.nuthatch.filteringutility.common;

import ru.nuthatch.filteringutility.fio.FileConsumer;
import ru.nuthatch.filteringutility.fio.FileProducer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Filter {

    private final ExecuteParameters parameters = ExecuteParameters.getInstance();

    BlockingQueue<String> integersQueue = new LinkedBlockingQueue<>();
    BlockingQueue<String> floatsQueue = new LinkedBlockingQueue<>();
    BlockingQueue<String> stringsQueue = new LinkedBlockingQueue<>();

    public void filter() {

        List<Thread> threads = new ArrayList<>();
        parameters.getFileList()
                .forEach(file -> {
                    threads.add(new Thread(new FileProducer(integersQueue, floatsQueue, stringsQueue, file)));
                });
        Thread integersConsumer = new Thread(new FileConsumer(integersQueue, parameters.getIntegersFile(),
                parameters.isAppendIfFileExists()));
        Thread floatsConsumer = new Thread(new FileConsumer(floatsQueue, parameters.getFloatsFile(),
                parameters.isAppendIfFileExists()));
        Thread stringsConsumer = new Thread(new FileConsumer(stringsQueue, parameters.getStringsFile(),
                parameters.isAppendIfFileExists()));

        threads.forEach(Thread::start);
        integersConsumer.start();
        floatsConsumer.start();
        stringsConsumer.start();

        try {
            threads.forEach(thread -> {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            integersConsumer.join();
            floatsConsumer.join();
            stringsConsumer.join();
        } catch (InterruptedException exception) {
            System.err.println(exception.getMessage());
        }
    }
}
