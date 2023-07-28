package ru.practicum.ewm.error;

public class FieldUniqueException extends RuntimeException {
    public FieldUniqueException(String message) {
        super(message);
    }
}
