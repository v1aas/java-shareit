package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    public User getUser(int id);

    public List<User> getUsers();

    public User postUser(User user);

    public User patchUser(int id, User user);

    public User deleteUser(int id);
}
