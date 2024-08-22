package ru.nuthatch.filteringutility.fio;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
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
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, appendIfFileExist))) {
            String line;
            while ((line = queue.poll(100, TimeUnit.MILLISECONDS)) != null) {
                bw.write(line);
                bw.newLine();
                setStatistics(line);
            }
        } catch (IOException | InterruptedException e) {
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
