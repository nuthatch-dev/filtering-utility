package ru.nuthatch.filter;

import org.apache.commons.cli.ParseException;
import ru.nuthatch.filter.core.Filter;
import ru.nuthatch.filter.core.SetupParameters;
import ru.nuthatch.filter.exceptions.NoInputFilePresentException;

public class Application {
    public static void main(String[] args) {

        try {
            // Установка параметров
            SetupParameters setupParameters = new SetupParameters();
            setupParameters.setup(args);

            // Выполнение основной части
            Filter filter = new Filter();
            filter.filter();

            // Вывод статистики
            System.out.println(filter.getStatistics());

        } catch (ParseException | NoInputFilePresentException exception) {
            System.err.println(exception.getMessage());
        }

    }
}
