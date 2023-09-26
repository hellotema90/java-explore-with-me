package ru.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequest {
    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 2, max = 250, message = "Размер имени должен быть >=2 и <=250 символов")
    private String name;
    @NotBlank(message = "Имя не может быть пустым")
    @Email(message = "Email должен быть в формате Email")
    @Size(min = 6, max = 254, message = "Размер email должен быть >=6 и <=254 символов")
    private String email;
}
