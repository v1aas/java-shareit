package ru.practicum.shareit.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String mess) {
        super(mess);
    }
}