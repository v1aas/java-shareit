package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final HashMap<Integer, User> userMap = new HashMap<>();
    private int id;

    @Override
    public User getUser(int id) {
        return userMap.get(id);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public User postUser(User user) {
        nameAndEmailValidation(user);
        repeatedEmailValidation(user);
        user.setId(++id);
        userMap.put(id, user);
        return user;
    }

    @Override
    public User patchUser(int id, User user) {
        user.setId(id);
        repeatedEmailValidation(user);
        if (!userMap.containsKey(id)) {
            throw new ValidationException("Пользователя с таким id нет");
        }
        if (user.getName() != null) {
            userMap.get(id).setName(user.getName());
        }
        if (user.getEmail() != null) {
            userMap.get(id).setEmail(user.getEmail());
        }
        return userMap.get(id);
    }

    @Override
    public User deleteUser(int id) {
        User user = userMap.get(id);
        userMap.remove(id);
        return user;
    }

    private void nameAndEmailValidation(User user) {
        if (user.getEmail() == null || user.getName() == null) {
            throw new ResourceNotFoundException("Имя или email не заполнены");
        }
    }

    private void repeatedEmailValidation(User user) {
        for (User us : userMap.values()) {
            if (user.getId() == us.getId()) {
                continue;
            }
            if (us.getEmail().equals(user.getEmail())) {
                throw new ValidationException("Такой email уже существует");
            }
        }
    }
}
