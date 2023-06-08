package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    public ItemDto getItem(int id);

    public List<ItemDto> getItems(int ownerId);

    public Item postItem(int ownerId, ItemDto item);

    public Item patchItem(int ownerId, int id, ItemDto item);

    public Item deleteItem(int ownerId, int id);

    public List<ItemDto> searchItem(String text);
}
