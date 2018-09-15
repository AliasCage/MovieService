package ru.aliascage.movie_service.exception;

public class NotFoundGenreException extends RuntimeException {

    public NotFoundGenreException(String genre) {
        super(String.format("Not found genre: %s", genre));
    }
}
