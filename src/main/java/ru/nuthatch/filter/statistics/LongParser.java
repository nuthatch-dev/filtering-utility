package ru.nuthatch.filter.statistics;

public class LongParser implements CommonParser<Long> {

    @Override
    public Long parse(String value) {
        return Long.parseLong(value);
    }
}
