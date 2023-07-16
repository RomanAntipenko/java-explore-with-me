package ru.practicum.ewm.stats.errorhandling;

public class IncorrectDateException extends RuntimeException {
    public IncorrectDateException(String message) {
        super(message);
    }
}
