package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compRepo;
    private final EventRepository eventRepo;

    @Override
    public CompilationDto save(NewCompilationDto dto) {
        Set<Event> events = fetchEvents(dto.getEvents());
        Compilation saved = compRepo.save(CompilationMapper.toEntity(dto, events));
        return CompilationMapper.toDto(saved, mapToEventShorts(events));
    }

    @Override
    public void delete(Long compId) {
        if (!compRepo.existsById(compId)) {
            throw new NotFoundException("Compilation not found");
        }
        compRepo.deleteById(compId);
    }

    @Override
    public CompilationDto update(Long compId, UpdateCompilationRequest dto) {
        Compilation comp = compRepo.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation not found"));

        if (dto.getTitle() != null) comp.setTitle(dto.getTitle());
        if (dto.getPinned() != null) comp.setPinned(dto.getPinned());
        if (dto.getEvents() != null) {
            Set<Event> events = fetchEvents(dto.getEvents());
            comp.setEvents(events);
        }

        return CompilationMapper.toDto(compRepo.save(comp), mapToEventShorts(comp.getEvents()));
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getById(Long id) {
        Compilation comp = compRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Compilation not found"));
        return CompilationMapper.toDto(comp, mapToEventShorts(comp.getEvents()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getAll(Boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Compilation> comps = pinned == null ?
                compRepo.findAll(pageable).getContent() :
                compRepo.findAll((root, query, cb) -> cb.equal(root.get("pinned"), pinned), pageable).getContent();

        return comps.stream()
                .map(c -> CompilationMapper.toDto(c, mapToEventShorts(c.getEvents())))
                .collect(Collectors.toList());
    }

    private Set<Event> fetchEvents(Set<Long> ids) {
        return ids == null || ids.isEmpty()
                ? Collections.emptySet()
                : new HashSet<>(eventRepo.findAllById(ids));
    }

    private Set<EventShortDto> mapToEventShorts(Set<Event> events) {
        return events.stream()
                .map(EventMapper::toShortDto)
                .collect(Collectors.toSet());
    }
}

