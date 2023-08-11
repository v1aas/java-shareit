package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceDB;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceDBTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserServiceDB service;

    @Test
    void testGetUser() {
        int userId = 1;
        User user = new User(userId, "Testman", "testman12@test.com");

        Mockito.when(repository.getById(userId)).thenReturn(user);
        UserDto result = service.getUser(userId);

        assertEquals(userId, result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void testGetUsers() {
        List<User> users = new ArrayList<>();
        users.add(new User(1, "Testman", "testman12@test.com"));
        users.add(new User(2, "Testman22", "testman123@test.com"));

        Mockito.when(repository.findAll()).thenReturn(users);
        List<UserDto> result = service.getUsers();

        assertEquals(users.size(), result.size());
        for (int i = 0; i < users.size(); i++) {
            assertEquals(users.get(i).getId(), result.get(i).getId());
            assertEquals(users.get(i).getName(), result.get(i).getName());
            assertEquals(users.get(i).getEmail(), result.get(i).getEmail());
        }
    }

    @Test
    void testPostUser() {
        UserDto newUserDto = new UserDto(null, "Testman", "testman12@test.com");
        User newUser = new User();
        newUser.setId(1);
        newUser.setName(newUserDto.getName());
        newUser.setEmail(newUserDto.getEmail());

        Mockito.when(repository.save(Mockito.any(User.class))).thenReturn(newUser);
        UserDto result = service.postUser(newUserDto);

        assertNotNull(result.getId());
        assertEquals(newUserDto.getName(), result.getName());
        assertEquals(newUserDto.getEmail(), result.getEmail());
    }

    @Test
    void testPatchUser() {
        int userId = 1;
        User updatedUser = new User(userId, "Updated Testman", "updatedemail@test.com");
        User oldUser = new User(userId, "Testman", "testman12@test.com");

        Mockito.when(repository.findById(userId)).thenReturn(Optional.of(oldUser));
        Mockito.when(repository.save(updatedUser)).thenReturn(updatedUser);
        UserDto result = service.patchUser(userId, UserMapper.toUserDto(updatedUser));

        assertEquals(userId, result.getId());
        assertEquals(updatedUser.getName(), result.getName());
        assertEquals(updatedUser.getEmail(), result.getEmail());
    }

    @Test
    void testDeleteUser() {
        int userId = 1;
        User userToDelete = new User(userId, "John Doe", "john.doe@example.com");

        Mockito.when(repository.getById(userId)).thenReturn(userToDelete);
        UserDto result = service.deleteUser(userId);

        assertEquals(userId, result.getId());
        assertEquals(userToDelete.getName(), result.getName());
        assertEquals(userToDelete.getEmail(), result.getEmail());
        Mockito.verify(repository, Mockito.times(1)).deleteById(userId);
    }

    @Test
    void testPostUserValidationException() {
        UserDto userDto = new UserDto();

        ValidationException exception = assertThrows(ValidationException.class, () -> service.postUser(userDto));
        assertEquals("Нужно заполнить все поля!", exception.getMessage());
    }

    @Test
    void testPostUser_ValidationException_InvalidEmail() {
        UserDto userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setEmail("invalidemail.com");

        ValidationException exception = assertThrows(ValidationException.class, () -> service.postUser(userDto));
        assertEquals("Почта некорректна", exception.getMessage());
    }
}