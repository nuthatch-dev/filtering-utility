package ru.nuthatch.filteringutility.common;

import org.apache.commons.cli.*;
import ru.nuthatch.filteringutility.exceptions.NoInputFilePresentException;

import java.util.Arrays;

public class SetupParameters {

    /**
     * Парсинг аргументов командной строки, установка параметров работы приложения
     *
     * @param args - аргументы, массив String
     */
    public void setup(String[] args) {

        Options options = new Options();

        // Опции командной строки
        Option resultPath = Option.builder("o")
                .longOpt("output-path")
                .argName("resultPath")
                .hasArg(true)
                .desc("output result path")
                .build();
        Option filesPrefix = Option.builder("p")
                .longOpt("prefix")
                .argName("filesPrefix")
                .hasArg(true)
                .desc("output file names prefix")
                .build();
        Option fileList = Option.builder("if")
                .longOpt("input-files")
                .hasArg(true)
                .desc("input file names separated by commas")
                .build();
        fileList.setArgs(Option.UNLIMITED_VALUES);

        options.addOption(resultPath);
        options.addOption(filesPrefix);
        options.addOption(fileList);
        options.addOption("a", "add-result", false, "add result to exist files");
        options.addOption("f", "full", false, "full report");
        options.addOption("s", "short", false, "short report");

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        // Парсинг выполнен успешно
        boolean optionsNotParsed = true;

        while (optionsNotParsed) {
            try {
                CommandLine cmd = parser.parse(options, args);

                // Установка параметров работы приложения
                ExecuteParameters parameters = ExecuteParameters.getInstance();

                if (cmd.hasOption("if")) {
                    parameters.setFileList(Arrays.stream(cmd.getOptionValues("if")).toList());
                } else {
                    // Остановка выполнения при отсутствии имен файлов входных данных в аргументах
                    throw new NoInputFilePresentException();
                }
                parameters.setResultPath(cmd.getOptionValue("o"));
                parameters.setFilesPrefix(cmd.getOptionValue("p"));
                parameters.setAddIfFileExists(cmd.hasOption("a"));
                parameters.setFullStatistic(cmd.hasOption("f"));
                parameters.setShortStatistic(cmd.hasOption("s"));

                optionsNotParsed = false;

            }
            catch (UnrecognizedOptionException exception) {
                System.err.println("Игнорируем неопознанный аргумент командной строки: " + exception.getMessage());
                // Удаляем неопознанный аргумент
                String wrongOption = exception.getOption();
                args = Arrays.stream(args).filter(a -> !a.equals(wrongOption)).toList().toArray(new String[0]);
            }
            catch (ParseException exception) {
                System.err.println("Неверно заданные аргументы командной строки: " + exception.getMessage()
                        + "\nДальнейшее выполнение невозможно");
                formatter.printHelp(
                        "java -jar filtering-utility-[VERSION].jar [options] -if file_01 file_02 ...",
                        options
                );
                throw new RuntimeException(exception);
            }
        }
    }

}
