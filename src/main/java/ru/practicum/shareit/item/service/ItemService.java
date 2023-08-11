package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    public ItemDto getItem(int userId, int itemId);

    public List<ItemDto> getItems(int ownerId, int from, int size);

    public ItemDto postItem(int ownerId, ItemDto item);

    public ItemDto patchItem(int ownerId, int id, ItemDto item);

    public ItemDto deleteItem(int ownerId, int id);

    public List<ItemDto> searchItem(String text, int from, int size);

    public CommentDto postComment(int userId, int itemid, CommentDto commentDto);
}
