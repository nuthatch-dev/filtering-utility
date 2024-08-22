package ru.nuthatch.filter.core;

import org.apache.commons.cli.*;
import ru.nuthatch.filter.common.InfoLevel;
import ru.nuthatch.filter.exceptions.NoInputFilePresentException;

import java.util.Arrays;

/**
 * Установка параметров приложения в соответствии с переданными аргументами
 */
public class SetupParameters {

    /**
     * Парсинг аргументов командной строки, установка параметров работы приложения.
     * Проверка на наличие передаваемых приложению файлов входных данных. При отсутствии
     * файлов - генерация исключения NoInputFilePresentException.
     * При некорректно заданной опции - удаление опции из массива, продолжение выполнения.
     *
     * @throws NoInputFilePresentException отсутствуют файлы входных данных
     * @throws ParseException ошибка парсинга аргументов командной строки
     *
     * @param args аргументы, массив String
     */
    public void setup(String[] args) throws ParseException {

        Options options = new Options();

        // Опции командной строки
        Option resultPath = Option.builder("o")
                .longOpt("output-path")
                .argName("PATH")
                .hasArg(true)
                .desc("output result path")
                .build();
        Option filesPrefix = Option.builder("p")
                .longOpt("prefix")
                .argName("PREFIX")
                .hasArg(true)
                .desc("output file names prefix")
                .build();
        Option fileList = Option.builder("if")
                .longOpt("input-files")
                .argName("FILES")
                .hasArg(true)
                .desc("input file names separated by space as 'file_01 file_02 ...'")
                .build();
        fileList.setArgs(Option.UNLIMITED_VALUES);

        options.addOption(resultPath);
        options.addOption(filesPrefix);
        options.addOption(fileList);
        options.addOption("h", "help", false, "show this help");
        options.addOption("a", "add-result", false, "add result to exist files");
        options.addOption("f", "full", false, "full report");
        options.addOption("s", "short", false, "short report");

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        // Парсинг выполнен успешно
        boolean optionsNotParsed = true;

        while (optionsNotParsed) {
            try {
                // Парсинг аргументов, установка параметров работы приложения
                ExecuteParameters parameters = ExecuteParameters.getInstance();
                CommandLine cmd = parser.parse(options, args);

                if (cmd.hasOption("h")) {
                    formatter.printHelp(
                            "java -jar filtering-utility-[VERSION].jar [options] -if file_01 file_02 ...",
                            options
                    );
                }
                if (cmd.hasOption("if")) {
                    parameters.setFileList(Arrays.stream(cmd.getOptionValues("if")).toList());
                } else {
                    // Остановка выполнения при отсутствии имен файлов входных данных в аргументах
                    throw new NoInputFilePresentException();
                }
                parameters.setResultPath(cmd.getOptionValue("o"));
                parameters.setFilesPrefix(cmd.getOptionValue("p"));
                parameters.setAppendIfFileExists(cmd.hasOption("a"));
                if (cmd.hasOption("f") || cmd.hasOption("s")) {
                    if (cmd.hasOption("f")) {
                        parameters.setInfoLevel(InfoLevel.FULL);
                    } else {
                        parameters.setInfoLevel(InfoLevel.SHORT);
                    }
                } else {
                    parameters.setInfoLevel(InfoLevel.DO_NOT_PROVIDE);
                }
                /*
                Если парсинг удачный, завершаем цикл.
                Иначе удаляем неопознанный аргумент и повторяем попытку
                 */
                optionsNotParsed = false;
            }
            catch (UnrecognizedOptionException exception) {
                System.err.println("Игнорируем неопознанный аргумент командной строки: " + exception.getMessage());
                // Удаляем неопознанный аргумент из массива, повторно устанавливаем параметры
                String wrongOption = exception.getOption();
                args = Arrays.stream(args).filter(a -> !a.equals(wrongOption)).toList().toArray(new String[0]);
            }
            catch (ParseException exception) {
                System.err.println("Неверно заданные аргументы командной строки: " + exception.getMessage()
                        + "\nДальнейшее выполнение невозможно");
                throw exception;
            }
        }
    }

}
