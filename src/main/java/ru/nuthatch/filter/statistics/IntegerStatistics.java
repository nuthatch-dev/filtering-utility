package ru.nuthatch.filter.statistics;

import ru.nuthatch.filter.common.InfoLevel;

public class IntegerStatistics implements CommonStatistics {

    private int elementCount = 0;
    private Long max = Long.MIN_VALUE;
    private Long min = Long.MAX_VALUE;
    private Long sum = 0L;
    private Long average;

    @Override
    public void calculate(Object value) {
        elementCount = elementCount + 1;
        Long element = (Long) value;
        if (element > max) {
            max = element;
        }
        if (element < min) {
            min = element;
        }
        sum = sum + element;
        average = sum/elementCount;
    }

    @Override
    public String getStatistics(InfoLevel level) {
        return switch (level) {
            case FULL -> {
                StringBuilder sb = new StringBuilder();
                sb.append("Количество записанных элементов: ").append(elementCount).append("\n");
                sb.append("Минимальное значение: ").append(min).append("\n");
                sb.append("Максимальное значение: ").append(max).append("\n");
                sb.append("Сумма элементов: ").append(sum).append("\n");
                sb.append("Среднее значение: ").append(average);
                yield sb.toString();
            }
            case SHORT -> "Количество записанных элементов: " + elementCount;
            case DO_NOT_PROVIDE -> "";
        };
    }
}
