package ru.practicum.compilation.dto;

import lombok.*;
import ru.practicum.event.dto.EventShortDto;

import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class CompilationDto {
    private Long id;
    private Boolean pinned;
    private String title;
    private Set<EventShortDto> events;
}