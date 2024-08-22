package ru.nuthatch.filteringutility.statistics;

@FunctionalInterface
public interface CommonParser<R> {

    R parse(String value);
}
