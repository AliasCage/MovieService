package ru.aliascage.movie_service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import ru.aliascage.movie_service.exception.IllegalPersonCountException;
import ru.aliascage.movie_service.exception.NotFoundGenreException;
import ru.aliascage.movie_service.model.Error;

import java.net.UnknownHostException;

@Slf4j
@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler({IllegalPersonCountException.class, NotFoundGenreException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Error handleProtocolExceptions(RuntimeException ex) {
        log.error("Not found exception", ex);
        return new Error("data.NotFound", ex.getMessage());
    }

    @ExceptionHandler({HttpClientErrorException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Error handleProtocolExceptions(HttpClientErrorException ex) {
        log.error("Not found exception", ex);
        String code = "unexpected.Exception";
        switch (ex.getStatusCode()) {
            case UNAUTHORIZED:
                code = "invalid.ApiKey";
                break;
            case NOT_FOUND:
                code = "invalid.URL";
        }
        return new Error(code, ex.getMessage());
    }

    @ExceptionHandler({UnknownHostException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Error handleProtocolExceptions(UnknownHostException ex) {
        log.error("Unknown host", ex);
        return new Error("invalid.Url", ex.getMessage());
    }

}
