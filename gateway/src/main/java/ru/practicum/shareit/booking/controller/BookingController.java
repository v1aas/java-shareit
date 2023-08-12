package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.BookingRequestDTO;
import ru.practicum.shareit.booking.state.BookingStateRequest;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Slf4j
@Validated
public class BookingController {

    private final BookingClient client;

    @PostMapping
    public ResponseEntity<Object> postBookingRequest(@RequestHeader("X-Sharer-User-Id") int userId,
                                                     @RequestBody BookingRequestDTO bookingDto) {
        log.info("Post booking request: {}", bookingDto);
        return client.createRequest(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> postApproveBooking(@PathVariable int bookingId,
                                                     @RequestHeader("X-Sharer-User-Id") int userId,
                                                     @RequestParam boolean approved) {
        log.info("Approve booking request: {}, {}", bookingId, approved);
        return client.postApproveBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingReqeust(@PathVariable int bookingId,
                                                    @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Get booking: {}", bookingId);
        return client.getBookingRequest(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingRequestForUser(@RequestHeader("X-Sharer-User-Id") int userId,
                                                              @RequestParam(defaultValue = "ALL") String state,
                                                              @PositiveOrZero @RequestParam(value = "from",
                                                                      defaultValue = "0") int from,
                                                              @Positive @RequestParam(value = "size",
                                                                      defaultValue = "10")
                                                              int size) {
        BookingStateRequest stateRequest = BookingStateRequest.isValid(state);
        log.info("Get booking for user: {}. from: {}; size: {}", userId, from, size);
        return client.getAllBookingRequestForUser(userId, stateRequest, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingRequestForOwner(@RequestHeader("X-Sharer-User-Id") int userId,
                                                               @RequestParam(defaultValue = "ALL") String state,
                                                               @PositiveOrZero @RequestParam(value = "from",
                                                                       defaultValue = "0") int from,
                                                               @Positive @RequestParam(value = "size",
                                                                       defaultValue = "10")
                                                               int size) {
        BookingStateRequest stateRequest = BookingStateRequest.isValid(state);
        log.info("Get booking for owner: {}. from: {}; size: {}", userId, from, size);
        return client.getAllBookingRequestForOwner(userId, stateRequest, from, size);
    }
}