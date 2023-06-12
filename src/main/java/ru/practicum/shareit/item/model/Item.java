package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Item {
    private int id;
    private String name;
    private String description;
    private Boolean available;
    private int owner;
    private String request;
}
