package ru.practicum.category.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CategoryDto {
    private Long id;
    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 1, max = 50, message = "Размер имени должен быть >=1 и <=50 символов")
    private String name;
}
