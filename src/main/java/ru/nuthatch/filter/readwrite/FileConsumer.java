package ru.nuthatch.filter.readwrite;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Запись данных в файл, передача статистики
 */
public class FileConsumer implements Runnable {

    private final Long POLL_TIMEOUT = 100L;

    private final BlockingQueue<String> queue;
    private final File file;
    private final Boolean appendIfFileExist;

    // Сбор статистики
    private final PropertyChangeSupport statistics;
    private String statisticsLine;

    public FileConsumer(BlockingQueue<String> queue,
                        File file,
                        Boolean appendIfFileExist) {
        this.queue = queue;
        this.file = file;
        this.appendIfFileExist = appendIfFileExist;
        // Определяем класс как наблюдаемый объект для сбора статистики
        this.statistics = new PropertyChangeSupport(this);
    }

    @Override
    public void run() {
        try {
            String line;
            /*
             Ожидаем первый элемент в очереди.
             Если не поступил, файл результатов для данного типа не создается
             */
            if ((line = queue.poll(POLL_TIMEOUT, TimeUnit.MILLISECONDS)) != null) {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, appendIfFileExist))) {
                    while (line != null) {
                        bw.write(line);
                        bw.newLine();
                        setStatistics(line);
                        line = queue.poll(POLL_TIMEOUT, TimeUnit.MILLISECONDS);
                    }
                } catch (IOException exception) {
                    System.err.println("Ошибка при сохранении в файл: " + exception.getMessage() +
                            ". Данные не будут добавлены в файл результатов");
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    Методы для работы с наблюдателями
     */
    public void addListener(PropertyChangeListener listener) {
        statistics.addPropertyChangeListener(listener);
    }

    public void removeListener(PropertyChangeListener listener) {
        statistics.removePropertyChangeListener(listener);
    }

    public void setStatistics(String value) {
        statistics.firePropertyChange("statisticsLine", statisticsLine, value);
        statisticsLine = value;
    }
}
