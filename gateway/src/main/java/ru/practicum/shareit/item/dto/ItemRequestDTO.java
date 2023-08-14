package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingRequestDTO;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDTO {
    long id;
    @NotBlank
    @NonNull
    String name;
    @NonNull
    @NotBlank
    String description;
    Boolean available;
    BookingRequestDTO lastBooking;
    BookingRequestDTO nextBooking;
    List<CommentRequestDTO> comments;
    Integer requestId;
}