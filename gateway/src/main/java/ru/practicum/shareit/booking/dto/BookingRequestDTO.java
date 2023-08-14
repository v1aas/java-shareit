package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.state.BookingStateRequest;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestDTO {
    private Integer itemId;
    @NonNull
    private LocalDateTime start;
    @NonNull
    private LocalDateTime end;
    private BookingStateRequest status;
    private Integer bookerId;
    private String itemName;
}
