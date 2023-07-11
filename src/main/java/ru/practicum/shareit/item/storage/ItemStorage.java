/*package ru.practicum.shareit.item.storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class ItemStorage {
    private final HashMap<Integer, Item> itemMap = new HashMap<>();
    private final HashMap<Integer, List<Item>> ownersItemsMap = new HashMap<>();
    private int id;
    private final UserStorage userStorage;

    @Autowired
    public ItemStorage(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Item getItem(int id) {
        return itemMap.get(id);
    }

    public List<Item> getItems(int ownerId) {
        return ownersItemsMap.get(ownerId);
    }

    public Item postItem(int ownerId, ItemDto item) {
        itemDtoValidation(item);
        if (userStorage.getUser(ownerId) == null) {
            throw new NullPointerException("Такого пользователя нет");
        }
        Item newItem = new Item(
                ++id,
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null
        );
        itemMap.put(id, newItem);

        List<Item> userItems = ownersItemsMap.getOrDefault(ownerId, new ArrayList<>());
        userItems.add(newItem);
        ownersItemsMap.put(ownerId, userItems);
        return newItem;
    }

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

    public Item deleteItem(int ownerId, int id) {
        Item item = itemMap.get(id);
        if (ownerId == itemMap.get(id).getOwner()) {
            ownersItemsMap.get(ownerId).remove(itemMap.get(id));
            itemMap.remove(id);
        } else {
            throw new ResourceNotFoundException("Только владелец вещи может её удалить");
        }
        return item;
    }

    public List<Item> searchItem(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        ArrayList<Item> items = new ArrayList<>();
        for (Item item : itemMap.values()) {
            if (item.getAvailable()) {
                if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                    items.add(item);
                } else if (item.getDescription().toLowerCase().contains(text.toLowerCase())) {
                    items.add(item);
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
*/