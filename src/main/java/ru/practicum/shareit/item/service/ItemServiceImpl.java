//package ru.practicum.shareit.item.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import ru.practicum.shareit.item.dto.ItemDto;
//import ru.practicum.shareit.item.mapper.ItemMapper;
//import ru.practicum.shareit.item.model.Item;
//import ru.practicum.shareit.item.storage.ItemStorage;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ItemServiceImpl implements ItemService {
//
//    private final ItemStorage itemStorage;
//
//    @Autowired
//    public ItemServiceImpl(ItemStorage itemStorage) {
//        this.itemStorage = itemStorage;
//    }
//
//    @Override
//    public ItemDto getItem(int id) {
//        return ItemMapper.toItemDto(itemStorage.getItem(id));
//    }
//
//    @Override
//    public List<ItemDto> getItems(int ownerId) {
//        ArrayList<ItemDto> items = new ArrayList<>();
//        for (Item item : itemStorage.getItems(ownerId)) {
//            items.add(ItemMapper.toItemDto(item));
//        }
//        return items;
//    }
//
//    @Override
//    public ItemDto postItem(int ownerId, ItemDto item) {
//        return ItemMapper.toItemDto(itemStorage.postItem(ownerId, item));
//    }
//
//    @Override
//    public ItemDto patchItem(int ownerId, int id, ItemDto item) {
//        return ItemMapper.toItemDto(itemStorage.patchItem(ownerId, id, item));
//    }
//
//    @Override
//    public ItemDto deleteItem(int ownerId, int id) {
//        return ItemMapper.toItemDto(itemStorage.deleteItem(ownerId, id));
//    }
//
//    @Override
//    public List<ItemDto> searchItem(String text) {
//        ArrayList<ItemDto> items = new ArrayList<>();
//        for (Item item : itemStorage.searchItem(text)) {
//            items.add(ItemMapper.toItemDto(item));
//        }
//        return items;
//    }
//}
