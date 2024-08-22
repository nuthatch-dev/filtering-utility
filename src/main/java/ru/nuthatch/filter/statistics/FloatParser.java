package ru.nuthatch.filter.statistics;

public class FloatParser implements CommonParser<Float> {

    @Override
    public Float parse(String value) {
        return Float.parseFloat(value);
    }
}
