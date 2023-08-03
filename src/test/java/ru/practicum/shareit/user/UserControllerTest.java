package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService service;

    @InjectMocks
    private UserController controller;

    private MockMvc mvc;

    private final ObjectMapper mapper = new ObjectMapper();

    User user1 = new User();
    User user2 = new User();
    UserDto userDto1;
    UserDto userDto2;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
        user1.setId(1);
        user1.setName("Testman");
        user1.setEmail("testman12@test.com");
        user2.setId(2);
        user2.setName("Testman2");
        user2.setEmail("testman123@test.com");
        userDto1 = UserMapper.toUserDto(user1);
        userDto2 = UserMapper.toUserDto(user2);
    }

    @Test
    void testGetUsers() throws Exception {
        List<UserDto> users = new ArrayList<>();
        users.add(userDto1);
        users.add(userDto2);

        Mockito.when(service.getUsers()).thenReturn(users);

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Testman"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("Testman2"));
    }

    @Test
    void testGetUser() throws Exception {
        Mockito.when(service.getUser(1)).thenReturn(userDto1);

        mvc.perform(get("/users/{id}", 1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Testman"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("testman12@test.com"));
    }

    @Test
    void testPostUser() throws Exception {
        UserDto savedUser = new UserDto(null, "Testman", "testman12@test.com");

        Mockito.when(service.postUser(savedUser)).thenReturn(userDto1);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(savedUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Testman"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("testman12@test.com"));
    }

    @Test
    void testPatchUser() throws Exception {
        UserDto updatedUser = new UserDto(1, "Updated Testman", "uptestman12@test.com");

        Mockito.when(service.patchUser(1, updatedUser)).thenReturn(updatedUser);

        mvc.perform(patch("/users/{id}", 1)
                        .content(mapper.writeValueAsString(updatedUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Updated Testman"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").
                        value("uptestman12@test.com"));
    }

    @Test
    void testDeleteUser() throws Exception {
        Mockito.when(service.deleteUser(1)).thenReturn(userDto1);

        mvc.perform(delete("/users/{id}", 1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Testman"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("testman12@test.com"));
    }
}