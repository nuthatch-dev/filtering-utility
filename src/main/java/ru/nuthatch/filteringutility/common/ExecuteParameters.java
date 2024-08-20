package ru.nuthatch.filteringutility.common;

import lombok.Getter;
import lombok.Setter;

/**
 * Параметры выполнения приложения
 */
public class ExecuteParameters {

    private final String BASE_PATH = "./";
    private final String BASE_INTEGERS_FILE_NAME = "integers.txt";
    private final String BASE_FLOATS_FILE_NAME = "floats.txt";
    private final String BASE_STRINGS_FILE_NAME = "strings.txt";

    private static ExecuteParameters instance;

    public static ExecuteParameters getInstance() {
        if (instance == null) {
            instance = new ExecuteParameters();
        }
        return instance;
    }

    @Getter
    @Setter
    private String resultPath = BASE_PATH;

    @Setter
    private String filesPrefix;

    @Getter
    private String integersFileName = filesPrefix + BASE_INTEGERS_FILE_NAME;

    @Getter
    private String floatsFileName = filesPrefix + BASE_FLOATS_FILE_NAME;

    @Getter
    private String stringsFileName = filesPrefix + BASE_STRINGS_FILE_NAME;

    @Getter
    @Setter
    private boolean overwriteFiles = true;

    @Getter
    @Setter
    private boolean getFullStatistic = false;

    @Getter
    @Setter
    private boolean getShortStatistic = false;
}
