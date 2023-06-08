package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.error.ErrorResponse;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") int ownerId) {
        return itemService.getItems(ownerId);
    }

    @GetMapping("/{id}")
    public ItemDto getItem(@PathVariable int id) {
        return itemService.getItem(id);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemForSearch(@RequestParam(required = false) String text) {
        return itemService.searchItem(text);
    }

    @PostMapping
    public Item postItem(@RequestHeader("X-Sharer-User-Id") int ownerId, @RequestBody ItemDto itemDto) {
        return itemService.postItem(ownerId, itemDto);
    }

    @PatchMapping("/{id}")
    public Item patchItem(@RequestHeader("X-Sharer-User-Id") int ownerId, @PathVariable int id,
                          @RequestBody ItemDto itemDto) {
        return itemService.patchItem(ownerId, id, itemDto);
    }

    @DeleteMapping("/{id}")
    public Item deleteItem(@RequestHeader("X-Sharer-User-Id") int ownerId, @PathVariable int id) {
        return itemService.deleteItem(ownerId, id);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse validationExp(final ValidationException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse reqExp(final ResourceNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse reqExp(final NullPointerException e) {
        return new ErrorResponse(e.getMessage());
    }
}
