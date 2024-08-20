package ru.nuthatch.filteringutility.common;

import org.apache.commons.cli.*;

public class SetupParameters {

    /**
     * Парсинг аргументов командной строки, установка параметров работы приложения
     * @param args - аргументы, массив String
     */
    public void setup(String[] args) {

        Options options = new Options();

        // Опции командной строки
        Option resultPath = Option.builder("o")
                .longOpt("output-path")
                .argName("resultPath")
                .hasArg()
                .desc("output result path")
                .build();
        Option filesPrefix = Option.builder("p")
                .longOpt("prefix")
                .argName("filesPrefix")
                .hasArg()
                .desc("output files prefix")
                .build();

        options.addOption(resultPath);
        options.addOption(filesPrefix);
        options.addOption("a", "add-result", false, "add result to exist files");
        options.addOption("f", "full", false, "full report");
        options.addOption("s", "short", false, "short report");

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        try {
            CommandLine commandLine = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println("Неверно заданные аргументы командной строки: " + e.getMessage());
            formatter.printHelp(
                    "java -jar filtering-utility-[VERSION].jar [options] -if [file_01, file_02 ...]",
                    options
            );
        }
    }

}
