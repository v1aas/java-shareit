package ru.practicum.shareit.booking.state;

import ru.practicum.shareit.exception.ValidationException;

public enum BookingStateRequest {
    ALL,
    REJECTED,
    WAITING,
    FUTURE,
    PAST,
    CURRENT;

    public static BookingStateRequest isValid(String state) {
        for (BookingStateRequest stateRequest : BookingStateRequest.values()) {
            if (stateRequest.name().equals(state)) {
                return stateRequest;
            }
        }
        throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
    }
}