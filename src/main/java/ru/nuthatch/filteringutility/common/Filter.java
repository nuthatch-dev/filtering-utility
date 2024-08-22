package ru.nuthatch.filteringutility.common;

import ru.nuthatch.filteringutility.fio.FileConsumer;
import ru.nuthatch.filteringutility.fio.FileProducer;
import ru.nuthatch.filteringutility.statistics.FloatStatistics;
import ru.nuthatch.filteringutility.statistics.StatisticsListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Filter {

    private final ExecuteParameters parameters = ExecuteParameters.getInstance();

    BlockingQueue<String> integersQueue = new LinkedBlockingQueue<>();
    BlockingQueue<String> floatsQueue = new LinkedBlockingQueue<>();
    BlockingQueue<String> stringsQueue = new LinkedBlockingQueue<>();

    StatisticsListener integersListener = new StatisticsListener();
    StatisticsListener floatsListener = new StatisticsListener();
    StatisticsListener stringsListener = new StatisticsListener();

    public void filter() {

        List<Thread> producerThreads = new ArrayList<>();
        parameters.getFileList()
                .forEach(file -> {
                    producerThreads.add(new Thread(new FileProducer(integersQueue, floatsQueue, stringsQueue, file)));
                });

        FileConsumer integersConsumer = new FileConsumer(integersQueue, parameters.getIntegersFile(),
                parameters.isAppendIfFileExists());
        integersConsumer.addListener(integersListener);
        Thread integersThread = new Thread(integersConsumer);

        FileConsumer floatsConsumer = new FileConsumer(floatsQueue, parameters.getFloatsFile(),
                parameters.isAppendIfFileExists());
        floatsConsumer.addListener(floatsListener);
        Thread floatsThread = new Thread(floatsConsumer);

        FileConsumer stringsConsumer = new FileConsumer(stringsQueue, parameters.getStringsFile(),
                parameters.isAppendIfFileExists());
        stringsConsumer.addListener(stringsListener);
        Thread stringsThread = new Thread(stringsConsumer);

        producerThreads.forEach(Thread::start);
        integersThread.start();
        floatsThread.start();
        stringsThread.start();

        try {
            producerThreads.forEach(thread -> {
                try {
                    thread.join();
                } catch (InterruptedException exception) {
                    System.err.println(exception.getMessage());
                }
            });
            integersThread.join();
            floatsThread.join();
            stringsThread.join();

        } catch (InterruptedException exception) {
            System.err.println(exception.getMessage());
        }
    }

    public String getStatistics() {
        if (parameters.getInfoLevel() == InfoLevel.DO_NOT_PROVIDE) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("  Статистика выполнения").append("\n");
            sb.append("======================================").append("\n");
            sb.append("* для целых чисел:").append("\n");
            sb.append(integersListener.getStatistics(parameters.getInfoLevel())).append("\n");
            sb.append("--------------------------------------").append("\n");
            sb.append("* для вещественных чисел:").append("\n");
            sb.append(floatsListener.getStatistics(parameters.getInfoLevel())).append("\n");
            sb.append("--------------------------------------").append("\n");
            sb.append("* для строк:").append("\n");
            sb.append(stringsListener.getStatistics(parameters.getInfoLevel())).append("\n");
            sb.append("--------------------------------------").append("\n");
            return sb.toString();
        }
    }
}
