package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceDB implements ItemService {

    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto getItem(int userId, int itemId) {
        Item item = repository.getById(itemId);
        ItemDto itemDto = ItemMapper.toItemDto(repository.getById(itemId));
        if (getNextBookingByItem(item) != null && userId == item.getOwner().getId()) {
            itemDto.setNextBooking(BookingMapper.toBookingDto(getNextBookingByItem(item)));
        }
        if (getLastBookingByItem(item) != null && userId == item.getOwner().getId()) {
            itemDto.setLastBooking(BookingMapper.toBookingDto(getLastBookingByItem(item)));
        }
        List<CommentDto> commentsByItem = new ArrayList<>();
        if (commentRepository.findAllByItem(item) != null) {
            for (Comment comment : commentRepository.findAllByItem(item)) {
                CommentDto commentDto = CommentMapper.toCommentDto(comment);
                commentsByItem.add(commentDto);
            }
            itemDto.setComments(commentsByItem);
        }
        return itemDto;
    }

    @Override
    public List<ItemDto> getItems(int ownerId, int from, int size) {
        ArrayList<ItemDto> items = new ArrayList<>();
        List<CommentDto> commentsByItem = new ArrayList<>();
        for (Item item : repository.findAllByOwnerOrderById(userRepository.getById(ownerId),
                PageRequest.of(from, size))) {
            ItemDto itemDto = ItemMapper.toItemDto(item);
            if (getLastBookingByItem(item) != null) {
                itemDto.setLastBooking(BookingMapper.toBookingDto(getLastBookingByItem(item)));
            }
            if (getNextBookingByItem(item) != null) {
                itemDto.setNextBooking(BookingMapper.toBookingDto(getNextBookingByItem(item)));
            }
            for (Comment comment : commentRepository.findAllByItem(item)) {
                commentsByItem.add(CommentMapper.toCommentDto(comment));
            }
            itemDto.setComments(commentsByItem);
            items.add(itemDto);
        }
        return items;
    }

    @Override
    public ItemDto postItem(int ownerId, ItemDto item) {
        if (item.getName().isEmpty() || item.getDescription() == null || item.getAvailable() == null) {
            throw new ValidationException("Нужно заполнить все поля!");
        }
        if (userRepository.findById(ownerId).isEmpty()) {
            throw new NullPointerException("Такого пользователя нет");
        }
        Item newItem = new Item();
        newItem.setName(item.getName());
        newItem.setDescription(item.getDescription());
        newItem.setAvailable(item.getAvailable());
        newItem.setOwner(userRepository.getById(ownerId));
        if (item.getRequestId() != null) {
            newItem.setRequestId(item.getRequestId());
        }
        return ItemMapper.toItemDto(repository.save(newItem));
    }

    @Override
    public ItemDto patchItem(int ownerId, int id, ItemDto item) {
        if (!(ownerId == repository.getOwnerById(id).getId())) {
            throw new NullPointerException("Изменять вещь может только владелец");
        }
        Item existsItem = repository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Такой вещи не существует"));
        if (item.getName() != null) {
            existsItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            existsItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            existsItem.setAvailable(item.getAvailable());
        }
        repository.save(existsItem);
        return ItemMapper.toItemDto(existsItem);
    }

    @Override
    public ItemDto deleteItem(int ownerId, int id) {
        ItemDto itemDto = ItemMapper.toItemDto(repository.getById(id));
        repository.deleteById(id);
        return itemDto;
    }

    @Override
    public List<ItemDto> searchItem(String text, int from, int size) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        List<Item> items = repository.searchItems(text, PageRequest.of(from, size)).toList();
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items) {
            itemsDto.add(ItemMapper.toItemDto(item));
        }
        return itemsDto;
    }

    @Override
    public CommentDto postComment(int userId, int itemId, CommentDto commentDto) {
        if (commentDto.getText().isEmpty()) {
            throw new ValidationException("Текст не должен быть пустой");
        }
        Comment comment = CommentMapper.toComment(commentDto);
        User author = userRepository.getById(userId);
        Item item = repository.getById(itemId);
        Booking booking = bookingRepository.findFirstByItemAndBookerAndStatusAndEndBefore(item, author,
                BookingStatus.APPROVED, LocalDateTime.now());
        if (booking == null) {
            throw new ValidationException("Пользователь должен был воспользоваться вещью");
        } else {
            comment.setItem(item);
            comment.setAuthor(author);
            comment.setCreated(LocalDateTime.now());
            return CommentMapper.toCommentDto(commentRepository.save(comment));
        }
    }

    public Booking getNextBookingByItem(Item item) {
        return bookingRepository.findFirstByItemAndStatusAndStartAfterOrderByStart(item,
                BookingStatus.APPROVED, LocalDateTime.now());
    }

    public Booking getLastBookingByItem(Item item) {
        return bookingRepository.findFirstByItemAndStatusAndStartBeforeOrderByStartDesc(item,
                BookingStatus.APPROVED, LocalDateTime.now());
    }
}