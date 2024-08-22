package ru.nuthatch.filter.statistics;

import ru.nuthatch.filter.common.InfoLevel;

public class StringStatistics implements CommonStatistics {

    private int elementCount = 0;
    private int maxLength = 0;
    private int minLength;

    @Override
    public void calculate(Object value) {
        String element = (String) value;
        elementCount = elementCount + 1;
        if (elementCount == 1) {
            minLength = element.length();
        }
        int size = element.length();
        if (size > maxLength) {
            maxLength = size;
        }
        if (size < minLength) {
            minLength = size;
        }
    }

    @Override
    public String getStatistics(InfoLevel level) {
        return switch (level) {
            case FULL -> {
                StringBuilder sb = new StringBuilder();
                sb.append("Количество записанных элементов: ").append(elementCount).append("\n");
                sb.append("Размер минимальной строки: ").append(minLength).append("\n");
                sb.append("Размер максимальной строки: ").append(maxLength);
                yield sb.toString();
            }
            case SHORT -> "Количество записанных элементов: " + elementCount;
            case DO_NOT_PROVIDE -> "";
        };
    }
}
