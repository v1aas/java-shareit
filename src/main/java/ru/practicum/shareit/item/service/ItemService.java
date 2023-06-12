package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    public ItemDto getItem(int id);

    public List<ItemDto> getItems(int ownerId);

    public ItemDto postItem(int ownerId, ItemDto item);

    public ItemDto patchItem(int ownerId, int id, ItemDto item);

    public ItemDto deleteItem(int ownerId, int id);

    public List<ItemDto> searchItem(String text);
}
