package ru.aliascage.movie_service.exception;

public class IllegalPersonCountException extends RuntimeException {

    public IllegalPersonCountException(String name, int count) {
        super(String.format("Found %s persons with name: %s", count, name));
    }
}
