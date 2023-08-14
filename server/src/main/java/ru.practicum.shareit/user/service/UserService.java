package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    public UserDto getUser(int id);

    public List<UserDto> getUsers();

    public UserDto postUser(UserDto user);

    public UserDto patchUser(int id, UserDto user);

    public UserDto deleteUser(int id);
}
