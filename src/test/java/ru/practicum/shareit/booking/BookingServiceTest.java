package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceDB;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void testGetAllBookingRequestForUserStateWaiting() {
        String state = "WAITING";

        List<Booking> bookings = new ArrayList<>();
        booking1.setStatus(BookingStatus.WAITING);
        bookings.add(booking1);

        List<BookingFullDto> expectedBookingsDto = new ArrayList<>();
        for (Booking book : bookings) {
            expectedBookingsDto.add(BookingMapper.toBookingFullDto(book));
        }

        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        Mockito.when(userRepository.getById(1)).thenReturn(user1);
        Mockito.when(repository.findByBookerAndStatus(Mockito.any(User.class), Mockito.eq(BookingStatus.WAITING),
                Mockito.any(Pageable.class))).thenReturn(new PageImpl<>(bookings));

        List<BookingFullDto> result = service.getAllBookingRequestForUser(1, state, 0, 10);

        assertEquals(expectedBookingsDto.size(), result.size());
        assertEquals(expectedBookingsDto, result);
    }

    @Test
    void testGetAllBookingRequestForUserStateRejected() {
        String state = "REJECTED";

        List<Booking> bookings = new ArrayList<>();
        booking1.setStatus(BookingStatus.REJECTED);
        bookings.add(booking1);

        List<BookingFullDto> expectedBookingsDto = new ArrayList<>();
        for (Booking book : bookings) {
            expectedBookingsDto.add(BookingMapper.toBookingFullDto(book));
        }

        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        Mockito.when(userRepository.getById(1)).thenReturn(user1);
        Mockito.when(repository.findByBookerAndStatus(Mockito.any(User.class), Mockito.eq(BookingStatus.REJECTED),
                Mockito.any(Pageable.class))).thenReturn(new PageImpl<>(bookings));

        List<BookingFullDto> result = service.getAllBookingRequestForUser(1, state, 0, 10);

        assertEquals(expectedBookingsDto.size(), result.size());
        assertEquals(expectedBookingsDto, result);
    }

    @Test
    void testGetAllBookingRequestForUserStatePast() {
        String state = "PAST";

        List<Booking> bookings = new ArrayList<>();
        booking1.setStatus(BookingStatus.APPROVED);
        bookings.add(booking1);

        List<BookingFullDto> expectedBookingsDto = new ArrayList<>();
        for (Booking book : bookings) {
            expectedBookingsDto.add(BookingMapper.toBookingFullDto(book));
        }

        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        Mockito.when(repository.getBookingByUserIdAndFinishAfterNow(Mockito.anyInt(), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(bookings));

        List<BookingFullDto> result = service.getAllBookingRequestForUser(1, state, 0, 10);

        assertEquals(expectedBookingsDto.size(), result.size());
        assertEquals(expectedBookingsDto, result);
    }

    @Test
    void testPostRequestItemOccupied() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(2);
        Mockito.when(itemRepository.getById(2)).thenReturn(item2);

        assertThrows(ValidationException.class, () -> service.postRequest(1, bookingDto));
    }

    @Test
    void testGetAllBookingRequestForUserFuture() {
        String state = "FUTURE";
        List<Booking> bookingsApprove = new ArrayList<>();
        List<Booking> bookingsWaiting = new ArrayList<>();
        bookingsWaiting.add(booking1);
        bookingsApprove.add(new Booking(2, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(3),
                item2, user1, BookingStatus.APPROVED));

        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        Mockito.when(userRepository.getById(1)).thenReturn(user1);
        Mockito.when(repository.findByBookerAndStatus(Mockito.any(User.class), Mockito.eq(BookingStatus.APPROVED),
                Mockito.any(Pageable.class))).thenReturn(new PageImpl<>(bookingsApprove));
        Mockito.when(repository.findByBookerAndStatus(Mockito.any(User.class), Mockito.eq(BookingStatus.WAITING),
                Mockito.any(Pageable.class))).thenReturn(new PageImpl<>(bookingsWaiting));

        List<BookingFullDto> result = service.getAllBookingRequestForUser(1, state, 0, 10);

        assertEquals(2, result.size());
    }

    @Test
    void testGetAllBookingRequestForOwnerFuture() {
        String state = "FUTURE";
        List<Item> ownerItems = new ArrayList<>();
        ownerItems.add(item1);
        ownerItems.add(new Item(3, "Item 3", "Description 3", true, user1, null));

        List<Booking> bookingsApprove = new ArrayList<>();
        List<Booking> bookingsWaiting = new ArrayList<>();
        bookingsApprove.add(new Booking(2, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(3),
                ownerItems.get(1), user1, BookingStatus.APPROVED));
        bookingsWaiting.add(booking1);

        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        Mockito.when(userRepository.getById(1)).thenReturn(user1);
        Mockito.when(itemRepository.findAllByOwnerOrderById(user1,
                PageRequest.of(0, 10))).thenReturn(new PageImpl<>(ownerItems));
        Mockito.when(repository.findByItemInAndStatus(ownerItems, BookingStatus.APPROVED,
                PageRequest.of(0 / 10, 10))).thenReturn(new PageImpl<>(bookingsApprove));
        Mockito.when(repository.findByItemInAndStatus(ownerItems, BookingStatus.WAITING,
                PageRequest.of(0 / 10, 10))).thenReturn(new PageImpl<>(bookingsWaiting));

        List<BookingFullDto> result = service.getAllBookingRequestForOwner(1, state, 0, 10);

        assertEquals(2, result.size());
    }

    @Test
    void testGetAllBookingRequestForUserAll() {
        String state = "ALL";
        Booking booking2 = new Booking();
        booking2.setId(2);
        booking2.setStart(LocalDateTime.now().plusDays(2));
        booking2.setEnd(LocalDateTime.now().plusDays(3));
        booking2.setBooker(user2);
        booking2.setStatus(BookingStatus.WAITING);
        booking2.setItem(item2);
        List<Booking> allBookings = new ArrayList<>();
        allBookings.add(booking1);
        allBookings.add(booking2);

        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        Mockito.when(userRepository.getById(1)).thenReturn(user1);
        Mockito.when(repository.findByBooker(userRepository.getById(1), PageRequest.of(0 / 10, 10,
                Sort.by(Sort.Direction.DESC, "start")))).thenReturn(new PageImpl<>(allBookings));

        List<BookingFullDto> result = service.getAllBookingRequestForUser(1, state, 0, 10);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(booking1.getId(), result.get(0).getId());
        assertEquals(booking1.getStatus(), result.get(0).getStatus());
        assertEquals(booking2.getId(), result.get(1).getId());
        assertEquals(booking2.getStatus(), result.get(1).getStatus());
    }

    @Test
    void testGetAllBookingRequestForOwnerAll() {
        String state = "ALL";
        Booking booking2 = new Booking();
        booking2.setId(2);
        booking2.setStart(LocalDateTime.now().plusDays(2));
        booking2.setEnd(LocalDateTime.now().plusDays(3));
        booking2.setBooker(user2);
        booking2.setItem(item2);
        booking2.setStatus(BookingStatus.WAITING);
        List<Booking> allBookings = new ArrayList<>();
        allBookings.add(booking1);
        allBookings.add(booking2);

        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        Mockito.when(userRepository.getById(1)).thenReturn(user1);
        Mockito.when(itemRepository.findAllByOwnerOrderById(userRepository.getById(1),
                PageRequest.of(0, 10))).thenReturn(new PageImpl<>(Arrays.asList(item1, item2)));
        List<Item> ownerItems = itemRepository.findAllByOwnerOrderById(userRepository.getById(1),
                PageRequest.of(0, 10)).toList();
        Mockito.when(repository.findByItemIn(ownerItems, PageRequest.of(0 / 10, 10,
                Sort.by(Sort.Direction.DESC, "start")))).thenReturn(new PageImpl<>(allBookings));

        List<BookingFullDto> result = service.getAllBookingRequestForOwner(1, state, 0, 10);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(booking1.getId(), result.get(0).getId());
        assertEquals(booking1.getStatus(), result.get(0).getStatus());
        assertEquals(booking2.getId(), result.get(1).getId());
        assertEquals(booking2.getStatus(), result.get(1).getStatus());
    }

    @Test
    void testGetAllBookingRequestForOwnerStateWaiting() {
        String state = "WAITING";
        List<Item> ownerItems = Arrays.asList(item1, item2);

        booking1.setStatus(BookingStatus.WAITING);
        Booking booking2 = new Booking();
        booking2.setId(2);
        booking2.setStatus(BookingStatus.WAITING);
        booking2.setStart(LocalDateTime.now().plusHours(2));
        booking2.setItem(item2);
        booking2.setBooker(user1);
        List<Booking> bookings = Arrays.asList(booking1, booking2);

        List<BookingFullDto> expectedBookingsDto = new ArrayList<>();
        for (Booking book : bookings) {
            expectedBookingsDto.add(BookingMapper.toBookingFullDto(book));
        }

        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        Mockito.when(userRepository.getById(1)).thenReturn(user1);
        Mockito.when(itemRepository.findAllByOwnerOrderById(Mockito.any(), Mockito.any()))
                .thenReturn(new PageImpl<>(ownerItems));
        Mockito.when(repository.findByItemInAndStatus(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(new PageImpl<>(bookings));

        List<BookingFullDto> result = service.getAllBookingRequestForOwner(1, state, 0, 10);
        assertEquals(expectedBookingsDto.size(), result.size());
        assertEquals(expectedBookingsDto, result);
    }

    @Test
    void testGetAllBookingRequestForOwnerStateRejected() {
        String state = "REJECTED";
        List<Item> ownerItems = Arrays.asList(item1, item2);

        booking1.setStatus(BookingStatus.REJECTED);
        Booking booking2 = new Booking();
        booking2.setId(2);
        booking2.setStatus(BookingStatus.REJECTED);
        booking2.setStart(LocalDateTime.now().minusHours(2));
        booking2.setItem(item2);
        booking2.setBooker(user1);
        List<Booking> bookings = Arrays.asList(booking1, booking2);

        List<BookingFullDto> expectedBookingsDto = new ArrayList<>();
        for (Booking book : bookings) {
            expectedBookingsDto.add(BookingMapper.toBookingFullDto(book));
        }

        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        Mockito.when(userRepository.getById(1)).thenReturn(user1);
        Mockito.when(itemRepository.findAllByOwnerOrderById(Mockito.any(), Mockito.any()))
                .thenReturn(new PageImpl<>(ownerItems));
        Mockito.when(repository.findByItemInAndStatus(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(new PageImpl<>(bookings));

        List<BookingFullDto> result = service.getAllBookingRequestForOwner(1, state, 0, 10);
        assertEquals(expectedBookingsDto.size(), result.size());
        assertEquals(expectedBookingsDto, result);
    }

    @Test
    void testGetAllBookingRequestForOwnerStatePast() {
        String state = "PAST";
        List<Item> ownerItems = Arrays.asList(item1, item2);

        booking1.setStatus(BookingStatus.APPROVED);
        Booking booking2 = new Booking();
        booking2.setId(2);
        booking2.setStatus(BookingStatus.APPROVED);
        booking2.setStart(LocalDateTime.now().plusHours(2));
        booking2.setItem(item2);
        booking2.setBooker(user1);
        List<Booking> bookings = Arrays.asList(booking1, booking2);

        List<BookingFullDto> expectedBookingsDto = new ArrayList<>();
        for (Booking book : bookings) {
            expectedBookingsDto.add(BookingMapper.toBookingFullDto(book));
        }

        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        Mockito.when(userRepository.getById(1)).thenReturn(user1);
        Mockito.when(itemRepository.findAllByOwnerOrderById(Mockito.any(), Mockito.any()))
                .thenReturn(new PageImpl<>(ownerItems));
        Mockito.when(repository.findAllByItemInAndStatusAndEndBeforeOrderByStartDesc(
                        Mockito.any(), Mockito.eq(BookingStatus.APPROVED), Mockito.any(), Mockito.any()))
                .thenReturn(new PageImpl<>(bookings));

        List<BookingFullDto> result = service.getAllBookingRequestForOwner(1, state, 0, 10);
        assertEquals(expectedBookingsDto.size(), result.size());
        assertEquals(expectedBookingsDto, result);
    }

    @Test
    void testGetAllBookingRequestForUserStateUnknown() {
        String state = "UNKNOWN";

        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(user1));

        assertThrows(ValidationException.class, () -> service
                .getAllBookingRequestForUser(1, state, 0, 10));
    }

    @Test
    void testGetAllBookingRequestForOwnerStateUnknown() {
        String state = "UNKNOWN";

        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(user1));

        assertThrows(ValidationException.class, () -> service
                .getAllBookingRequestForOwner(1, state, 0, 10));
    }

    @Test
    void testPostRequestUserNotFound() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(2);
        Mockito.when(itemRepository.getById(Mockito.anyInt())).thenReturn(item1);
        Mockito.when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NullPointerException.class, () -> service.postRequest(1, bookingDto));
    }

    @Test
    void testPostRequestInvalidTime() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(2);
        item2.setAvailable(true);
        Mockito.when(itemRepository.getById(2)).thenReturn(item2);
        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(new User()));

        LocalDateTime start = LocalDateTime.now().plusHours(2);
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);

        assertThrows(ValidationException.class, () -> service.postRequest(1, bookingDto));
    }
}