package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {
    @Mock
    private BookingService service;
    @InjectMocks
    private BookingController controller;
    private MockMvc mvc;
    private final ObjectMapper mapper = new ObjectMapper();

    Item item1 = new Item();
    Item item2 = new Item();
    User user1 = new User();
    User user2 = new User();
    Comment comment = new Comment();
    Booking booking1 = new Booking();

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
        mapper.registerModule(new JavaTimeModule());
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
    void testPostRequest() throws Exception {
        BookingDto bookingDto = BookingMapper.toBookingDto(booking1);
        BookingFullDto expectedBookingFullDto = new BookingFullDto();
        Mockito.when(service.postRequest(2, bookingDto)).thenReturn(expectedBookingFullDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", String.valueOf(2))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(expectedBookingFullDto)));
    }

    @Test
    void testPostApproveBooking() throws Exception {
        BookingFullDto expectedBookingFullDto = new BookingFullDto();
        Mockito.when(service.postApproveBooking(1, 2, true)).thenReturn(expectedBookingFullDto);

        mvc.perform(MockMvcRequestBuilders.patch("/bookings/{bookingId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2)
                        .param("approved", String.valueOf(true)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(expectedBookingFullDto)));
    }

    @Test
    void testGetBookingRequest() throws Exception {
        BookingFullDto expectedBookingFullDto = new BookingFullDto();
        Mockito.when(service.getBookingRequest(1, 2)).thenReturn(expectedBookingFullDto);

        mvc.perform(MockMvcRequestBuilders.get("/bookings/{bookingId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(expectedBookingFullDto)));
    }

    @Test
    void testGetAllBookingRequestForUser() throws Exception {
        String state = "CURRENT";
        List<BookingFullDto> expectedBookingFullDtos = new ArrayList<>();

        Mockito.when(service.getAllBookingRequestForUser(1, state, 0, 10)).
                thenReturn(expectedBookingFullDtos);

        mvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("state", state)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(expectedBookingFullDtos)));
    }

    @Test
    void testGetAllBookingRequestForOwner() throws Exception {
        String state = "CURRENT";
        List<BookingFullDto> expectedBookingFullDtos = new ArrayList<>();
        Mockito.when(service.getAllBookingRequestForOwner(1, state, 0, 10)).
                thenReturn(expectedBookingFullDtos);

        mvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("state", state)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(expectedBookingFullDtos)));
    }
}