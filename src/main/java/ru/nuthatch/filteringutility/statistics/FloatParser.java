package ru.nuthatch.filteringutility.statistics;

public class FloatParser implements CommonParser<Float> {

    @Override
    public Float parse(String value) {
        return Float.parseFloat(value);
    }
}
