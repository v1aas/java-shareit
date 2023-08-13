package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.UserRequestDTO;

import javax.validation.Valid;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient client;

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Get all users");
        return client.getUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable int id) {
        log.info("Get user {}", id);
        return client.getUser(id);
    }

    @PostMapping
    public ResponseEntity<Object> postUser(@Valid @RequestBody UserRequestDTO user) {
        log.info("Post user: {}", user);
        return client.createUser(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patchUser(@PathVariable int id, @RequestBody UserRequestDTO user) {
        log.info("Patch user: {}", id);
        return client.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable int id) {
        log.info("Delete user: {}", id);
        return client.deleteUser(id);
    }
}