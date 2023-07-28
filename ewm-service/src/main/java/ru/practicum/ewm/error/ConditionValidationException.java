package ru.practicum.ewm.error;

public class ConditionValidationException extends RuntimeException {
    public ConditionValidationException(String message) {
        super(message);
    }
}
