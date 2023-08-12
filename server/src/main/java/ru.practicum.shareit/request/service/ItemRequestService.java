package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    public ItemRequestDto postRequest(Integer userId, ItemRequestDto requestDto);

    public List<ItemRequestDto> getRequest(Integer userId);

    public List<ItemRequestDto> getAllRequest(Integer userId, Integer from, Integer size);

    public ItemRequestDto getRequestById(Integer requestId, Integer userId);
}
