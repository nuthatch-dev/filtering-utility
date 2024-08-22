package ru.nuthatch.filter.exceptions;

public class NoInputFilePresentException extends RuntimeException {

    private final String DEFAULT_MESSAGE = "Не заданы файлы с исходными данными. Дальнейшее выполнение невозможно";

    public NoInputFilePresentException() {
        throw new NoInputFilePresentException(DEFAULT_MESSAGE);
    }

    public NoInputFilePresentException(String message) {
        super(message);
    }

}
