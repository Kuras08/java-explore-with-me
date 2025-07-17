package ru.practicum.event.dto;

import lombok.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventShortDto {

    private Long id;
    private String title;
    private String annotation;
    private LocalDateTime eventDate;
    private boolean paid;
    private Long views;
    private UserShortDto initiator;
    private CategoryDto category;
    private int confirmedRequests;
}

