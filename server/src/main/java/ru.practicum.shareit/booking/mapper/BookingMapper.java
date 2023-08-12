package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking book) {
        return new BookingDto(book.getId(),
                book.getStart(),
                book.getEnd(),
                book.getStatus(),
                book.getBooker().getId(),
                book.getItem().getId(),
                book.getItem().getName());
    }

    public static BookingFullDto toBookingFullDto(Booking book) {
        return new BookingFullDto(book.getId(),
                book.getStart(),
                book.getEnd(),
                ItemMapper.toItemDto(book.getItem()),
                UserMapper.toUserDto(book.getBooker()),
                book.getStatus());
    }
}
