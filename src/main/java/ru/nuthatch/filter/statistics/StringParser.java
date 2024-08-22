package ru.nuthatch.filter.statistics;

public class StringParser implements CommonParser<String> {

    @Override
    public String parse(String value) {
        return value;
    }
}
