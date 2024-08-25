package ru.nuthatch.filter.core;

import ru.nuthatch.filter.common.ExtendedThreadPoolExecutor;
import ru.nuthatch.filter.common.InfoLevel;
import ru.nuthatch.filter.readwrite.FileConsumer;
import ru.nuthatch.filter.readwrite.FileProducer;
import ru.nuthatch.filter.statistics.StatisticsListener;

import java.util.concurrent.*;

/**
 * Core class. Запуск потоков для чтения/записи, сбор статистики.
 */
public class Filter {

    private final ExecuteParameters parameters = ExecuteParameters.getInstance();

    BlockingQueue<String> integersQueue = new LinkedBlockingQueue<>();
    BlockingQueue<String> floatsQueue = new LinkedBlockingQueue<>();
    BlockingQueue<String> stringsQueue = new LinkedBlockingQueue<>();

    StatisticsListener integersListener = new StatisticsListener();
    StatisticsListener floatsListener = new StatisticsListener();
    StatisticsListener stringsListener = new StatisticsListener();

    /**
     * Подготовка и запуск поставщиков {@code FileProducer} для чтения исходных данных -
     * отдельная задача для каждого переданного файла в списке.<p>
     * Подготовка и запуск потребителей {@code FileConsumer} для каждого из сохраняемых типов.<p>
     * Подключение наблюдателей для сбора статистики.
     */
    public void filter() {

        try (ExecutorService executorService = new ExtendedThreadPoolExecutor(0,
                Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>())) {

            // Запуск задач для поставщиков
            parameters.getFileList().forEach(file -> {
                executorService.submit(new FileProducer(integersQueue, floatsQueue, stringsQueue, file));
            });

            // Запуск задач для потребителей, подключение наблюдателей
            FileConsumer integersConsumer = new FileConsumer(integersQueue, parameters.getIntegersFile(),
                    parameters.isAppendIfFileExists());
            integersConsumer.addListener(integersListener);
            executorService.submit(integersConsumer);

            FileConsumer floatsConsumer = new FileConsumer(floatsQueue, parameters.getFloatsFile(),
                    parameters.isAppendIfFileExists());
            floatsConsumer.addListener(floatsListener);
            executorService.submit(floatsConsumer);

            FileConsumer stringsConsumer = new FileConsumer(stringsQueue, parameters.getStringsFile(),
                    parameters.isAppendIfFileExists());
            stringsConsumer.addListener(stringsListener);
            executorService.submit(stringsConsumer);

            executorService.shutdown();
        }
    }

    /**
     * Выдача статистики
     *
     * @return String
     */
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
