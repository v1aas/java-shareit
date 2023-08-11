package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceDB implements ItemRequestService {

    private final ItemRequestRepository repository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto postRequest(Integer userId, ItemRequestDto requestDto) {
        if (isValidUser(userId)) {
            throw new NullPointerException("Такого пользователя не существует!");
        }
        if (requestDto.getDescription() == null || requestDto.getDescription().isEmpty()) {
            throw new ValidationException("Описание не может быть пустым");
        }
        ItemRequest newRequest = new ItemRequest();
        newRequest.setRequestorId(userId);
        newRequest.setDescription(requestDto.getDescription());
        newRequest.setCreated(LocalDateTime.now());
        return ItemRequestMapper.toRequestDto(repository.save(newRequest));
    }

    @Override
    public List<ItemRequestDto> getRequest(Integer userId) {
        if (isValidUser(userId)) {
            throw new NullPointerException("Такого пользователя не существует!");
        }
        List<ItemRequestDto> requests = new ArrayList<>();
        for (ItemRequest request : repository.findByRequestorId(userId)) {
            request.setItems(itemRepository.findByRequestId(request.getId()));
            requests.add(ItemRequestMapper.toRequestDto(request));
        }
        return requests;
    }

    @Override
    public List<ItemRequestDto> getAllRequest(Integer userId, Integer from, Integer size) {
        if (from < 0 || size < 0 || from > size || size == 0) {
            throw new ValidationException("Неправильно указаны размеры");
        }
        List<ItemRequestDto> requests = new ArrayList<>();
        for (ItemRequest request : repository.findAll(PageRequest.of(from, size,
                Sort.by(Sort.Direction.ASC, "created"))).toList()) {
            if (userId.equals(request.getRequestorId())) {
                continue;
            }
            request.setItems(itemRepository.findByRequestId(request.getId()));
            requests.add(ItemRequestMapper.toRequestDto(request));
        }
        return requests;
    }

    @Override
    public ItemRequestDto getRequestById(Integer requestId, Integer userId) {
        if (repository.findById(requestId).isEmpty()) {
            throw new NullPointerException("Такого запроса нет");
        }
        if (isValidUser(userId)) {
            throw new NullPointerException("Такого пользователя не существует!");
        }
        ItemRequest itemRequest = repository.getById(requestId);
        itemRequest.setItems(itemRepository.findByRequestId(requestId));
        return ItemRequestMapper.toRequestDto(itemRequest);
    }

    private boolean isValidUser(Integer userId) {
        return userRepository.findById(userId).isEmpty();
    }
}
