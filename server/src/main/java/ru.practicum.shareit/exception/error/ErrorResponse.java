package ru.practicum.shareit.exception.error;

public class ErrorResponse {
    String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

    // геттеры необходимы, чтобы Spring Boot мог получить значения полей
    public String getError() {
        return error;
    }
}
