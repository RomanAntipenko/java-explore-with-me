package ru.practicum.ewm.stats.errorhandling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse incorrectDateHandler(final IncorrectDateException e) {
        log.error(e.getMessage() + ". Ошибка: " + e.getClass().getName());
        return new ErrorResponse(
                e.getMessage()
        );
    }
}
