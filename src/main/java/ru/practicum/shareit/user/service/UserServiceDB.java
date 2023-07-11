package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceDB implements UserService {
    private final UserRepository repository;

    @Override
    public UserDto getUser(int id) {
        return UserMapper.toUserDto(repository.getById(id));
    }

    @Override
    public List<UserDto> getUsers() {
        List<UserDto> usersDto = new ArrayList<>();
        List<User> users = repository.findAll();
        for (User user : users) {
            usersDto.add(UserMapper.toUserDto(user));
        }
        return usersDto;
    }

    @Override
    public UserDto postUser(User user) {
        if (user.getName() == null || user.getEmail() == null) {
            throw new ValidationException("Нужно заполнить все поля!");
        }
        repository.save(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto patchUser(int id, User user) {
        user.setId(id);
        if (repository.findByEmail(user.getEmail()) != null && !user.getEmail().equals(getUser(id).getEmail())) {
            repository.save(user);
        }
        User existsUser = repository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Такого пользователя не существует"));
        if (user.getName() != null) {
            existsUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            existsUser.setEmail(user.getEmail());
        }
        repository.save(existsUser);
        return UserMapper.toUserDto(existsUser);
    }

    @Override
    public UserDto deleteUser(int id) {
        UserDto user = UserMapper.toUserDto(repository.getById(id));
        repository.deleteById(id);
        return user;
    }
}
