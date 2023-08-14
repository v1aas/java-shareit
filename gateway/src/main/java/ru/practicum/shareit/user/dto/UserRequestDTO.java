package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDTO {
    Integer id;
    @NonNull
    @NotBlank
    String name;
    @NonNull
    @Email
    String email;
}
