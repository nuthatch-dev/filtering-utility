package ru.nuthatch.filteringutility.common;

import lombok.Getter;
import lombok.Setter;
import ru.nuthatch.filteringutility.exceptions.NoInputFilePresentException;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Параметры выполнения приложения
 */
public class ExecuteParameters {

    private final Path BASE_PATH = Path.of("./");
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

    public void dropParameters() {
        instance = null;
    }

    @Getter
    private Path resultPath = BASE_PATH;

    public void setResultPath(String pathString) {
        if (pathString != null) {
            try {
                resultPath = Paths.get(pathString);
            } catch (InvalidPathException | NullPointerException exception) {
                System.err.println("Неверно задан путь для сохранения результатов: " + exception.getMessage()
                        + "\nСохранение в текущую папку");
            }
        }
    }

    public void setFilesPrefix(String prefix) {
        if (prefix != null) {
            try {
                Paths.get(prefix);
                integersFileName = prefix + BASE_INTEGERS_FILE_NAME;
                floatsFileName = prefix + BASE_FLOATS_FILE_NAME;
                stringsFileName = prefix + BASE_STRINGS_FILE_NAME;
            } catch (InvalidPathException | NullPointerException exception) {
                System.err.println("Неверно задан префикс имен выходных файлов: " + exception.getMessage()
                        + "\nСохранение выполняется с именами файлов по умолчанию");
            }
        }
    }

    @Getter
    private String integersFileName = BASE_INTEGERS_FILE_NAME;

    @Getter
    private String floatsFileName = BASE_FLOATS_FILE_NAME;

    @Getter
    private String stringsFileName = BASE_STRINGS_FILE_NAME;

    @Getter
    @Setter
    private boolean addIfFileExists = false;

    @Getter
    @Setter
    private boolean fullStatistic = false;

    @Getter
    @Setter
    private boolean shortStatistic = false;

    @Getter
    private List<File> fileList = new ArrayList<>();

    public void setFileList(List<String> fileList) {
        // Проверяем наличие переданных файлов, удаляем несуществующие
        this.fileList = fileList.stream().map(File::new).filter(File::isFile).toList();
        if (this.fileList.isEmpty()) {
            throw new NoInputFilePresentException(
                    "Указанные файлы входных данных отсутствуют. Дальнейшее выполнение невозможно");
        }
    }
}
