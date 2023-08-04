package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceDB;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceDBTest {

    @Mock
    private ItemRepository repository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private ItemServiceDB service;

    Item item1 = new Item();
    Item item2 = new Item();
    User user1 = new User();
    User user2 = new User();
    Comment comment = new Comment();

    @BeforeEach
    void setUp() {
        item1.setId(1);
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setAvailable(true);
        item1.setOwner(user1);
        item2.setId(2);
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setAvailable(false);
        item2.setOwner(user2);
        user1.setId(1);
        user1.setName("Testman");
        user1.setEmail("testman12@test.com");
        user2.setId(2);
        user2.setName("Testman2");
        user2.setEmail("testman123@test.com");
        comment.setId(1);
        comment.setText("text");
        comment.setCreated(LocalDateTime.now().plusMinutes(1));
        comment.setAuthor(user1);
    }

    @Test
    void testGetItem() {
        Mockito.when(repository.getById(1)).thenReturn(item1);
        ItemDto result = service.getItem(1, 1);
        assertNotNull(result);
        assertEquals(item1.getId(), result.getId());
        assertEquals(item1.getName(), result.getName());
        assertEquals(item1.getDescription(), result.getDescription());
        assertEquals(item1.getAvailable(), result.getAvailable());
    }

    @Test
    void testGetItems() {
        repository.save(item1);
        repository.save(item2);
        userRepository.save(user1);
        userRepository.save(user2);
        Page pageList = new PageImpl(List.of(item1), PageRequest.of(1, 10), 2);
        Mockito.when(userRepository.getById(1)).thenReturn(user1);
        Mockito.when(repository.findAllByOwnerOrderById(user1, PageRequest.of(1, 10))).thenReturn(pageList);

        List<ItemDto> result = service.getItems(1, 1, 10);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testPostItem() {
        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        Mockito.when(userRepository.getById(1)).thenReturn(user1);
        Mockito.doReturn(item1).when(repository).save(Mockito.any(Item.class));
        ItemDto result = service.postItem(1, ItemMapper.toItemDto(item1));

        assertNotNull(result);
        assertEquals(item1.getName(), result.getName());
        assertEquals(item1.getDescription(), result.getDescription());
        assertEquals(item1.getAvailable(), result.getAvailable());
    }

    @Test
    void testPatchItem() {
        ItemDto updatedItemDto = new ItemDto();
        updatedItemDto.setName("Updated Item 1");
        updatedItemDto.setDescription("Updated Description of Item 1");
        updatedItemDto.setAvailable(false);

        Mockito.when(repository.getOwnerById(1)).thenReturn(user1);
        Mockito.when(repository.findById(1)).thenReturn(Optional.of(item1));
        ItemDto result = service.patchItem(1, 1, updatedItemDto);

        assertNotNull(result);
        assertEquals(updatedItemDto.getName(), result.getName());
        assertEquals(updatedItemDto.getDescription(), result.getDescription());
        assertEquals(updatedItemDto.getAvailable(), result.getAvailable());
    }

    @Test
    void testDeleteItem() {
        Mockito.when(repository.getById(1)).thenReturn(item1);
        ItemDto result = service.deleteItem(1, 1);

        assertNotNull(result);
        assertEquals(item1.getName(), result.getName());
        assertEquals(item1.getDescription(), result.getDescription());
        assertEquals(item1.getAvailable(), result.getAvailable());
        Mockito.verify(repository).deleteById(1);
    }

    @Test
    void testSearchItem() {
        String searchText = "deSCRIPtion";
        List<Item> itemsList = new ArrayList<>();
        itemsList.add(item1);
        itemsList.add(item2);

        Mockito.when(repository.searchItems(Mockito.eq(searchText), Mockito.any(PageRequest.class)))
                .thenReturn(new PageImpl<>(itemsList));
        List<ItemDto> result = service.searchItem(searchText, 1, 10);
        assertNotNull(result);
        assertEquals(itemsList.size(), result.size());
    }

    @Test
    void testPostComment() {
        CommentDto commentDto = CommentMapper.toCommentDto(comment);
        Booking booking1 = new Booking(1, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                item1, user1, BookingStatus.APPROVED);
        Mockito.when(userRepository.getById(1)).thenReturn(user1);
        Mockito.when(repository.getById(1)).thenReturn(item1);
        Mockito.when(bookingRepository.findFirstByItemAndBookerAndStatusAndEndBefore(Mockito.any(Item.class),
                Mockito.any(User.class), Mockito.any(BookingStatus.class),
                Mockito.any(LocalDateTime.class))).thenReturn(booking1);
        Mockito.doReturn(comment).when(commentRepository).save(Mockito.any(Comment.class));

        CommentDto result = service.postComment(1, 1, commentDto);
        assertNotNull(result);
        assertEquals(commentDto.getText(), result.getText());
    }

    @Test
    void testPatchItemResourceNotFoundException() {
        int ownerId = 1;
        int itemId = 123;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("New Item Name");

        Mockito.when(repository.getOwnerById(itemId)).thenReturn(user1);
        Mockito.when(repository.findById(itemId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> service.patchItem(ownerId, itemId, itemDto));
        assertEquals("Такой вещи не существует", exception.getMessage());
    }

    @Test
    void testPatchItemOwnerIsWrong() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Updated Item");
        itemDto.setDescription("Updated Description");

        Item existingItem = new Item();
        existingItem.setId(2);
        existingItem.setOwner(new User(3, "Testwoman", "@mail.com"));

        Mockito.when(repository.getOwnerById(2)).thenReturn(existingItem.getOwner());

        assertThrows(NullPointerException.class, () -> service.patchItem(1, 2, itemDto));
    }

    @Test
    void testPatchItemNotFound() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Updated Item");
        itemDto.setDescription("Updated Description");

        Mockito.when(repository.getOwnerById(2)).thenReturn(user1);
        Mockito.when(repository.findById(2)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.patchItem(1, 2, itemDto));
    }

    @Test
    public void testGetItemWithBookingsAndComments() {
        Comment comment2 = new Comment();
        comment2.setId(2);
        comment2.setText("Comment 2");
        comment2.setAuthor(user2);

        Booking booking = new Booking();
        booking.setId(1);
        booking.setBooker(user1);
        booking.setStart(comment.getCreated().plusDays(2));
        booking.setEnd(comment.getCreated().plusDays(3));
        booking.setItem(item1);

        List<Comment> comments = Arrays.asList(comment, comment2);

        Mockito.when(repository.getById(1)).thenReturn(item1);
        Mockito.when(commentRepository.findAllByItem(item1)).thenReturn(comments);
        Mockito.when(bookingRepository.findFirstByItemAndStatusAndStartAfterOrderByStart(Mockito.eq(item1),
                Mockito.eq(BookingStatus.APPROVED), Mockito.any())).thenReturn(booking);
        Mockito.when(bookingRepository.findFirstByItemAndStatusAndStartBeforeOrderByStartDesc(Mockito.eq(item1),
                Mockito.eq(BookingStatus.APPROVED), Mockito.any())).thenReturn(null);

        ItemDto result = service.getItem(1, 1);

        assertNotNull(result);
        assertEquals(item1.getId(), result.getId());
        assertEquals(item1.getName(), result.getName());
        assertEquals(item1.getDescription(), result.getDescription());

        assertEquals(booking.getId(), result.getNextBooking().getId());
        assertEquals(booking.getStart(), result.getNextBooking().getStart());
        assertEquals(booking.getEnd(), result.getNextBooking().getEnd());

        assertEquals(comments.size(), result.getComments().size());
        assertEquals(comments.get(0).getId(), result.getComments().get(0).getId());
        assertEquals(comments.get(0).getText(), result.getComments().get(0).getText());
        assertEquals(comments.get(1).getId(), result.getComments().get(1).getId());
        assertEquals(comments.get(1).getText(), result.getComments().get(1).getText());
    }
}