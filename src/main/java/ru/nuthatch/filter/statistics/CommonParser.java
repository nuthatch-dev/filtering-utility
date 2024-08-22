package ru.nuthatch.filter.statistics;

@FunctionalInterface
public interface CommonParser<R> {

    R parse(String value);
}
