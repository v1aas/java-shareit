package ru.practicum.shareit.request.contorller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.error.ErrorResponse;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService service;

    @PostMapping
    public ItemRequestDto postRequest(@RequestHeader("X-Sharer-User-Id") int userId,
                                      @RequestBody ItemRequestDto request) {
        return service.postRequest(userId, request);
    }

    @GetMapping
    public List<ItemRequestDto> getRequest(@RequestHeader("X-Sharer-User-Id") int userId) {
        return service.getRequest(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequest(@RequestHeader("X-Sharer-User-Id") int userId,
                                              @RequestParam(value = "from", defaultValue = "0") int from,
                                              @RequestParam(value = "size", defaultValue = "10") int size) {
        return service.getAllRequest(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@PathVariable Integer requestId,
                                         @RequestHeader("X-Sharer-User-Id") int userId) {
        return service.getRequestById(requestId, userId);
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
