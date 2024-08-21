package ru.nuthatch.filteringutility;

import ru.nuthatch.filteringutility.common.Filter;
import ru.nuthatch.filteringutility.common.SetupParameters;

public class Application {
    public static void main(String[] args) {

        SetupParameters setupParameters = new SetupParameters();
        setupParameters.setup(args);

        try {
            Filter filter = new Filter();
            filter.filter();
        } catch (Exception e) {
            System.err.println("Усе пропало" + e.getMessage());
        }

    }
}
