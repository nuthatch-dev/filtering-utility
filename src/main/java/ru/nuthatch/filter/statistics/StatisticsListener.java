package ru.nuthatch.filter.statistics;

import ru.nuthatch.filter.common.InfoLevel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Наблюдатель. Получение статистики по работе, передача для обработки и хранения
 */
public class StatisticsListener implements PropertyChangeListener {

    private final String INTEGER_REGEX = "-?[0-9]*";
    private CommonStatistics statistics;
    private CommonParser<?> parser;
    private boolean setupNotComplete = true;

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String value = (String) evt.getNewValue();
        /*
         На первом входящем значении настраиваем наблюдателя.
         Инициализируем соответствующими экземплярами класса парсер и статистику
         */
        if (setupNotComplete) {
            setupListener(value);
        }
        calculate(value);
    }

    private void calculate(String value) {
        statistics.calculate(parser.parse(value));
    }

    public String getStatistics(InfoLevel level) {
        return statistics.getStatistics(level);
    }

    private void setupListener(String value) {
        try {
            Float f = Float.parseFloat(value);
            if (value.matches(INTEGER_REGEX)) {
                statistics = new IntegerStatistics();
                parser = new LongParser();
            } else {
                statistics = new FloatStatistics();
                parser = new FloatParser();
            }
        } catch (NumberFormatException _) {
            statistics = new StringStatistics();
            parser = new StringParser();
        }
        setupNotComplete = false;
    }
}
