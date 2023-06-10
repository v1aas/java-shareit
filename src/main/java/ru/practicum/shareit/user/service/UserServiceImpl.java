package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public UserDto getUser(int id) {
        return UserMapper.toUserDto(userStorage.getUser(id));
    }

    @Override
    public List<UserDto> getUsers() {
        ArrayList<UserDto> users = new ArrayList<>();
        for (User user : userStorage.getUsers()) {
            users.add(UserMapper.toUserDto(user));
        }
        return users;
    }

    @Override
    public UserDto postUser(User user) {
        return UserMapper.toUserDto(userStorage.postUser(user));
    }

    @Override
    public UserDto patchUser(int id, User user) {
        return UserMapper.toUserDto(userStorage.patchUser(id, user));
    }

    @Override
    public UserDto deleteUser(int id) {
        return UserMapper.toUserDto(userStorage.deleteUser(id));
    }
}
