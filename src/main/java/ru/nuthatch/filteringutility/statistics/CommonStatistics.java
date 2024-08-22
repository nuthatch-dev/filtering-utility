package ru.nuthatch.filteringutility.statistics;

import ru.nuthatch.filteringutility.common.InfoLevel;

public interface CommonStatistics {

    void calculate(Object value);
    String getStatistics(InfoLevel level);
}
