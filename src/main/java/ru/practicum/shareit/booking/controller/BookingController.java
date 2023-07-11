package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.error.ErrorResponse;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingFullDto postReqeust(@RequestHeader("X-Sharer-User-Id") int userId,
                                      @RequestBody BookingDto bookingDto) {
        return bookingService.postRequest(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingFullDto postApproveBooking(@PathVariable int bookingId, @RequestHeader("X-Sharer-User-Id") int userId,
                                             @RequestParam boolean approved) {
        return bookingService.postApproveBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingFullDto getBookingReqeust(@PathVariable int bookingId, @RequestHeader("X-Sharer-User-Id") int userId) {
        return bookingService.getBookingRequest(bookingId, userId);
    }

    @GetMapping
    public List<BookingFullDto> getAllBookingRequestForUser(@RequestHeader("X-Sharer-User-Id") int userId,
                                                            @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllBookingRequestForUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingFullDto> getAllBookingRequestForOwner(@RequestHeader("X-Sharer-User-Id") int userId,
                                                             @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllBookingRequestForOwner(userId, state);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse reqExp(final ValidationException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse nullExp(final NullPointerException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse entityNotFoundExp(final EntityNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }
}
