package ru.practicum.compilation.mapper;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;

import java.util.Set;

public class CompilationMapper {

    public static CompilationDto toDto(Compilation comp, Set<EventShortDto> eventDtos) {
        CompilationDto dto = new CompilationDto();
        dto.setId(comp.getId());
        dto.setTitle(comp.getTitle());
        dto.setPinned(comp.isPinned());
        dto.setEvents(eventDtos);
        return dto;
    }

    public static Compilation toEntity(NewCompilationDto dto, Set<Event> events) {
        Compilation comp = new Compilation();
        comp.setTitle(dto.getTitle());
        comp.setPinned(dto.isPinned());
        comp.setEvents(events);
        return comp;
    }
}