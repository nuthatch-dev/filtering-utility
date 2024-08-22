package ru.nuthatch.filter.statistics;

import ru.nuthatch.filter.common.InfoLevel;

public interface CommonStatistics {

    void calculate(Object value);
    String getStatistics(InfoLevel level);
}
