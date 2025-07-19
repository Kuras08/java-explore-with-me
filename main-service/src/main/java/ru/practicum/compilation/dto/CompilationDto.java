package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.event.dto.EventShortDto;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDto {
    private Long id;
    private String title;
    private boolean pinned;
    private Set<EventShortDto> events;
}

