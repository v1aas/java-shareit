package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceDB;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    @Mock
    private ItemRequestRepository repository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private ItemRequestServiceDB service;

    Item item1 = new Item();
    Item item2 = new Item();
    User user1 = new User();
    User user2 = new User();
    Comment comment = new Comment();
    Booking booking1 = new Booking();

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
    public void testPostRequest() {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Test request description");

        ItemRequest newRequest = new ItemRequest();
        newRequest.setRequestorId(1);
        newRequest.setDescription(requestDto.getDescription());
        newRequest.setCreated(LocalDateTime.now());

        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        Mockito.when(repository.save(Mockito.any(ItemRequest.class))).thenReturn(newRequest);

        ItemRequestDto result = service.postRequest(1, requestDto);

        assertNotNull(result);
        assertEquals(requestDto.getDescription(), result.getDescription());
    }

    @Test
    public void testGetRequest() {
        Integer userId = 1;

        ItemRequest request1 = new ItemRequest();
        request1.setId(1);
        request1.setRequestorId(userId);
        request1.setDescription("Request 1");

        ItemRequest request2 = new ItemRequest();
        request2.setId(2);
        request2.setRequestorId(userId);
        request2.setDescription("Request 2");

        List<ItemRequest> requests = Arrays.asList(request1, request2);

        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        Mockito.when(repository.findByRequestorId(userId)).thenReturn(requests);
        Mockito.when(itemRepository.findByRequestId(1)).thenReturn(Collections.emptyList());
        Mockito.when(itemRepository.findByRequestId(2)).thenReturn(Collections.emptyList());

        List<ItemRequestDto> result = service.getRequest(1);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(request1.getDescription(), result.get(0).getDescription());
        assertEquals(request2.getDescription(), result.get(1).getDescription());
    }

    @Test
    public void testGetAllRequest() {
        ItemRequest request1 = new ItemRequest();
        request1.setId(1);
        request1.setRequestorId(1);
        request1.setDescription("Request 1");
        request1.setCreated(LocalDateTime.now().minusHours(1));

        ItemRequest request2 = new ItemRequest();
        request2.setId(2);
        request2.setRequestorId(1);
        request2.setDescription("Request 2");
        request2.setCreated(LocalDateTime.now().minusMinutes(30));

        List<ItemRequest> allRequests = Arrays.asList(request1, request2);

        Mockito.when(repository.findAll(Mockito.any(PageRequest.class))).thenReturn(new PageImpl<>(allRequests));
        Mockito.when(itemRepository.findByRequestId(Mockito.anyInt())).thenReturn(Collections.emptyList());

        List<ItemRequestDto> result = service.getAllRequest(2, 0, 10);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(request1.getDescription(), result.get(0).getDescription());
        assertEquals(request2.getDescription(), result.get(1).getDescription());
    }

    @Test
    public void testGetRequestById() {
        Integer requestId = 1;
        Integer userId = 2;

        ItemRequest request = new ItemRequest();
        request.setId(requestId);
        request.setRequestorId(userId);
        request.setDescription("Test request");

        Mockito.when(repository.findById(requestId)).thenReturn(Optional.of(request));
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        Mockito.when(itemRepository.findByRequestId(requestId)).thenReturn(Collections.emptyList());
        Mockito.when(repository.getById(1)).thenReturn(request);

        ItemRequestDto result = service.getRequestById(requestId, userId);

        assertNotNull(result);
        assertEquals(request.getDescription(), result.getDescription());
    }

    @Test
    void testPostRequestUserIdNull() {
        Integer userId = null;
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Test description");

        assertThrows(NullPointerException.class, () -> service.postRequest(userId, requestDto));
    }

    @Test
    void testPostRequestDescriptionNull() {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription(null);
        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(user1));

        assertThrows(ValidationException.class, () -> service.postRequest(10, requestDto));
    }
}