package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.error.ErrorResponse;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.persistence.EntityNotFoundException;
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
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") int ownerId,
                                  @RequestParam(value = "from", defaultValue = "0") int from,
                                  @RequestParam(value = "size", defaultValue = "10") int size) {
        return itemService.getItems(ownerId, from, size);
    }

    @GetMapping("/{id}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int id) {
        return itemService.getItem(userId, id);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemForSearch(@RequestParam(required = false) String text,
                                          @RequestParam(value = "from", defaultValue = "0") int from,
                                          @RequestParam(value = "size", defaultValue = "10") int size) {
        return itemService.searchItem(text, from, size);
    }

    @PostMapping
    public ItemDto postItem(@RequestHeader("X-Sharer-User-Id") int ownerId, @RequestBody ItemDto itemDto) {
        return itemService.postItem(ownerId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int itemId,
                                  @RequestBody CommentDto commentDto) {
        return itemService.postComment(userId, itemId, commentDto);
    }

    @PatchMapping("/{id}")
    public ItemDto patchItem(@RequestHeader("X-Sharer-User-Id") int ownerId, @PathVariable int id,
                             @RequestBody ItemDto itemDto) {
        return itemService.patchItem(ownerId, id, itemDto);
    }

    @DeleteMapping("/{id}")
    public ItemDto deleteItem(@RequestHeader("X-Sharer-User-Id") int ownerId, @PathVariable int id) {
        return itemService.deleteItem(ownerId, id);
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