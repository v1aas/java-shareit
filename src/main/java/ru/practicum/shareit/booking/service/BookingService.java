package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;

import java.util.List;

public interface BookingService {

    public BookingFullDto postRequest(int userId, BookingDto booking);

    public BookingFullDto postApproveBooking(int bookingId, int userId, boolean approve);

    public BookingFullDto getBookingRequest(int bookingId, int userId);

    public List<BookingFullDto> getAllBookingRequestForUser(int userId, String state);

    public List<BookingFullDto> getAllBookingRequestForOwner(int userId, String state);
}
