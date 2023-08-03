package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceDB;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private BookingRepository repository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private BookingServiceDB service;

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
        booking1.setId(1);
        booking1.setStart(LocalDateTime.now().plusDays(1));
        booking1.setEnd(LocalDateTime.now().plusDays(2));
        booking1.setItem(item1);
        booking1.setBooker(user1);
        booking1.setStatus(BookingStatus.WAITING);
    }

    @Test
    public void testPostRequest() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1);
        bookingDto.setStart(booking1.getStart());
        bookingDto.setEnd(booking1.getEnd());

        Mockito.when(itemRepository.getById(1)).thenReturn(item1);
        Mockito.when(userRepository.findById(2)).thenReturn(Optional.of(user2));
        Mockito.when(repository.save(Mockito.any(Booking.class))).thenReturn(booking1);
        BookingFullDto result = service.postRequest(2, bookingDto);

        assertNotNull(result);
        assertEquals(1, result.getItem().getId());
        assertEquals(1, result.getBooker().getId());
        assertEquals(BookingStatus.WAITING, result.getStatus());
    }

    @Test
    public void testPostApproveBooking() {
        Mockito.when(repository.save(booking1)).thenReturn(booking1);
        repository.save(booking1);
        Mockito.when(repository.getById(1)).thenReturn(booking1);

        BookingFullDto result = service.postApproveBooking(1, 1, true);
        assertNotNull(result);
        assertEquals(BookingStatus.APPROVED, result.getStatus());
    }

    @Test
    public void testGetBookingRequest() {
        Mockito.when(repository.getById(1)).thenReturn(booking1);
        BookingFullDto result = service.getBookingRequest(1, 1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(1, result.getBooker().getId());
    }

    @Test
    void testGetAllBookingRequestForUser() {
        String state = "CURRENT";
        Booking booking2 = new Booking(2, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(3), item2,
                user2, BookingStatus.WAITING);
        List<Booking> bookings = Arrays.asList(booking1, booking2);
        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(new User()));
        Mockito.when(repository.getCurrentByUserId(Mockito.anyInt(), Mockito.any()))
                .thenReturn(new PageImpl<>(bookings));

        List<BookingFullDto> expectedResults = bookings.stream()
                .map(BookingMapper::toBookingFullDto)
                .collect(Collectors.toList());

        List<BookingFullDto> actualResults = service.getAllBookingRequestForUser(1, state, 1, 10);
        assertEquals(expectedResults.size(), actualResults.size());
    }

    @Test
    void testGetAllBookingRequestForOwner() {
        String state = "CURRENT";
        List<Item> ownerItems = Arrays.asList(item1, item2);
        Booking booking2 = new Booking(2, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(3), item2,
                user2, BookingStatus.WAITING);
        List<Booking> bookings = Arrays.asList(booking1, booking2);
        Page pageList = new PageImpl(ownerItems, PageRequest.of(1, 10), 2);

        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(new User()));
        Mockito.when(itemRepository.findAllByOwnerOrderById(Mockito.any(), Mockito.any()))
                .thenReturn(pageList);
        Mockito.when(repository.findAllByItemInAndStartBeforeAndEndAfterOrderByStartDesc(
                        Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(new PageImpl<>(bookings));

        List<BookingFullDto> expectedResults = bookings.stream()
                .map(BookingMapper::toBookingFullDto)
                .collect(Collectors.toList());

        List<BookingFullDto> actualResults = service.getAllBookingRequestForOwner(1, state, 1, 10);
        assertEquals(expectedResults.size(), actualResults.size());
    }
}