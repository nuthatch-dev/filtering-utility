package ru.nuthatch.filteringutility.statistics;

import ru.nuthatch.filteringutility.common.InfoLevel;

public class FloatStatistics implements CommonStatistics {

    private int elementCount = 0;
    private Float max = Float.MIN_VALUE;
    private Float min = Float.MAX_VALUE;
    private Float sum = 0f;
    private Float average;

    @Override
    public void calculate(Object value) {
        elementCount = elementCount + 1;
        Float element = (Float) value;
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
