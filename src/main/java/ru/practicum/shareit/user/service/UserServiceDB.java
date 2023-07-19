package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
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
    public UserDto postUser(UserDto user) {
        if (user.getName() == null || user.getEmail() == null) {
            throw new ValidationException("Нужно заполнить все поля!");
        }
        if (!isValidEmail(user.getEmail())) {
            throw new ValidationException("Почта некорректна");
        }
        return UserMapper.toUserDto(repository.save(UserMapper.toUser(user)));
    }

    @Transactional
    @Override
    public UserDto patchUser(int id, UserDto user) {
        user.setId(id);
        User existsUser = repository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Такого пользователя не существует"));
        if (user.getName() != null) {
            existsUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            existsUser.setEmail(user.getEmail());
        }
        return UserMapper.toUserDto(repository.save(existsUser));
    }

    @Override
    public UserDto deleteUser(int id) {
        UserDto user = UserMapper.toUserDto(repository.getById(id));
        repository.deleteById(id);
        return user;
    }

    private boolean isValidEmail(String email) {
        if (email.contains("@")) {
            return true;
        } else {
            return false;
        }
    }
}
