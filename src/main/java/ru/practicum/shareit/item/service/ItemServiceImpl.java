package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    private final HashMap<Integer, Item> itemMap = new HashMap<>();
    private int id;
    @Autowired
    private UserService userService = new UserServiceImpl();

    @Override
    public ItemDto getItem(int id) {
        return ItemMapper.toItemDto(itemMap.get(id));
    }

    @Override
    public List<ItemDto> getItems(int ownerId) {
        ArrayList<ItemDto> items = new ArrayList<>();
        for (Item item : itemMap.values()) {
            if (item.getOwner() == ownerId) {
                items.add(ItemMapper.toItemDto(item));
            }
        }
        return items;
    }

    @Override
    public Item postItem(int ownerId, ItemDto item) {
        itemDtoValidation(item);
        if (userService.getUser(ownerId) == null) {
            throw new NullPointerException("Такого пользователя нет");
        }
        Item newItem = new Item(
                ++id,
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                ownerId,
                null
        );
        itemMap.put(id, newItem);
        return newItem;
    }

    @Override
    public Item patchItem(int ownerId, int id, ItemDto item) {
        if (!(ownerId == itemMap.get(id).getOwner())) {
            throw new NullPointerException("Изменять вещь может только владелец");
        }
        if (item.getName() != null) {
            itemMap.get(id).setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemMap.get(id).setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemMap.get(id).setAvailable(item.getAvailable());
        }
        return itemMap.get(id);
    }

    @Override
    public Item deleteItem(int ownerId, int id) {
        Item item = itemMap.get(id);
        if (ownerId == itemMap.get(id).getOwner()) {
            itemMap.remove(id);
        } else {
            throw new ResourceNotFoundException("Только владелец вещи может её удалить");
        }
        return item;
    }

    public List<ItemDto> searchItem(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        ArrayList<ItemDto> items = new ArrayList<>();
        for (Item item : itemMap.values()) {
            if (item.getAvailable()) {
                if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                    items.add(ItemMapper.toItemDto(item));
                } else if (item.getDescription().toLowerCase().contains(text.toLowerCase())) {
                    items.add(ItemMapper.toItemDto(item));
                }
            }
        }
        return items;
    }

    private void itemDtoValidation(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getDescription() == null || itemDto.getAvailable() == null) {
            throw new ResourceNotFoundException("Нужно заполнить: имя, описание, доступность");
        } else if (itemDto.getDescription().isEmpty() || itemDto.getName().isEmpty()) {
            throw new ResourceNotFoundException("Нужно заполнить: имя, описание, доступность");
        }
    }
}
