package ru.nuthatch.filteringutility.statistics;

public class LongParser implements CommonParser<Long> {

    @Override
    public Long parse(String value) {
        return Long.parseLong(value);
    }
}
