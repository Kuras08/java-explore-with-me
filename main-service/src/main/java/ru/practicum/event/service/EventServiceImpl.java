package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.*;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventSpecifications;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.Location;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.stat.service.StatsService;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final EventRepository eventRepo;
    private final UserRepository userRepo;
    private final CategoryRepository categoryRepo;
    private final RequestRepository requestRepo;
    private final StatsService statsService; // Важно!

    // PRIVATE --------------------------------------------

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto dto) {
        User user = getUser(userId);
        Category category = getCategory(dto.getCategory());

        if (dto.getEventDate() == null) {
            throw new ValidationException("eventDate must not be null");
        }
        if (dto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Event date must be at least 2 hours in the future.");
        }

        Event event = EventMapper.toEntity(dto, user, category);
        Event saved = eventRepo.save(event);

        return EventMapper.toFullDto(saved, 0, 0L);
    }

    @Override
    public List<EventShortDto> getUserEvents(Long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepo.findAllByInitiatorId(userId, pageable);
        List<Long> eventIds = events.stream().map(Event::getId).toList();
        Map<Long, Long> viewsMap = statsService.getViews(eventIds, true);

        return events.stream()
                .map(e -> EventMapper.toShortDto(e, getConfirmedRequests(e.getId()), viewsMap.getOrDefault(e.getId(), 0L)))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getUserEvent(Long userId, Long eventId) {
        Event event = eventRepo.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
        Map<Long, Long> viewsMap = statsService.getViews(List.of(eventId), true);
        long views = viewsMap.getOrDefault(eventId, 0L);
        return EventMapper.toFullDto(event, getConfirmedRequests(eventId), views);
    }

    @Override
    public EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest dto) {
        Event event = eventRepo.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Cannot edit a published event");
        }

        if (dto.getEventDate() != null && dto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Event date must be at least 2 hours in the future.");
        }

        updateEventFields(event, dto.getTitle(), dto.getAnnotation(), dto.getDescription(),
                dto.getEventDate(), dto.getLocation(), dto.getCategory(),
                dto.getPaid(), dto.getParticipantLimit(), dto.getRequestModeration());

        if (dto.getStateAction() == UpdateEventUserRequest.EventStateAction.SEND_TO_REVIEW) {
            event.setState(EventState.PENDING);
        } else if (dto.getStateAction() == UpdateEventUserRequest.EventStateAction.CANCEL_REVIEW) {
            event.setState(EventState.CANCELED);
        }

        Map<Long, Long> viewsMap = statsService.getViews(List.of(eventId), true);
        long views = viewsMap.getOrDefault(eventId, 0L);

        return EventMapper.toFullDto(eventRepo.save(event), getConfirmedRequests(eventId), views);
    }

    // ADMIN --------------------------------------------
    @Override
    public List<EventFullDto> searchEventsForAdmin(List<Long> users, List<String> states,
                                                   List<Long> categories, LocalDateTime rangeStart,
                                                   LocalDateTime rangeEnd, int from, int size) {
        Specification<Event> spec = EventSpecifications.adminSearch(users, states, categories, rangeStart, rangeEnd);
        Pageable pageable = PageRequest.of(from / size, size);
        Page<Event> eventsPage = eventRepo.findAll(spec, pageable);
        List<Event> events = eventsPage.getContent();
        List<Long> eventIds = events.stream().map(Event::getId).toList();
        Map<Long, Long> viewsMap = statsService.getViews(eventIds, true);

        return events.stream()
                .map(e -> EventMapper.toFullDto(e, getConfirmedRequests(e.getId()), viewsMap.getOrDefault(e.getId(), 0L)))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest dto) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if (dto.getStateAction() == UpdateEventAdminRequest.AdminStateAction.PUBLISH_EVENT) {
            LocalDateTime eventDateToCheck = dto.getEventDate() != null ? dto.getEventDate() : event.getEventDate();
            if (eventDateToCheck == null) {
                throw new ValidationException("eventDate must not be null when publishing event");
            }
            if (eventDateToCheck.isBefore(LocalDateTime.now().plusHours(1))) {
                throw new ValidationException("Event date must be at least 1 hour in the future.");
            }
            if (event.getState() != EventState.PENDING) {
                throw new ConflictException("Only pending events can be published.");
            }
            event.setState(EventState.PUBLISHED);
            event.setPublishedOn(LocalDateTime.now());
        } else if (dto.getStateAction() == UpdateEventAdminRequest.AdminStateAction.REJECT_EVENT) {
            if (event.getState() == EventState.PUBLISHED) {
                throw new ConflictException("Cannot reject a published event.");
            }
            event.setState(EventState.CANCELED);
        }

        updateEventFields(event, dto.getTitle(), dto.getAnnotation(), dto.getDescription(),
                dto.getEventDate(), dto.getLocation(), dto.getCategory(),
                dto.getPaid(), dto.getParticipantLimit(), dto.getRequestModeration());

        Map<Long, Long> viewsMap = statsService.getViews(List.of(eventId), true);
        long views = viewsMap.getOrDefault(eventId, 0L);

        return EventMapper.toFullDto(eventRepo.save(event), getConfirmedRequests(eventId), views);
    }

    // PUBLIC --------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getPublicEvents(String text, List<Long> categories, Boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               Boolean onlyAvailable, String sort, int from, int size,
                                               HttpServletRequest request) {
        Specification<Event> spec = EventSpecifications.publicSearch(text, categories, paid,
                rangeStart, rangeEnd, onlyAvailable);

        int realSize = size > 0 ? size : 10;
        int page = from / realSize;
        Pageable pageable = PageRequest.of(page, realSize);

        List<Event> events = eventRepo.findAll(spec, pageable).stream()
                .filter(e -> e.getState() == EventState.PUBLISHED)
                .collect(Collectors.toList());

        saveStats(request);

        List<Long> eventIds = events.stream().map(Event::getId).toList();
        Map<Long, Long> viewsMap = statsService.getViews(eventIds, true);

        return events.stream()
                .map(e -> {
                    long views = viewsMap.getOrDefault(e.getId(), 0L);
                    return EventMapper.toShortDto(e, getConfirmedRequests(e.getId()), views);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto getPublicEventById(Long id, HttpServletRequest request) {
        Event event = eventRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие с id " + id + " не найдено"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Событие не опубликовано");
        }

        saveStats(request);

        Map<Long, Long> viewsMap = statsService.getViews(List.of(event.getId()), true);
        long views = viewsMap.getOrDefault(event.getId(), 0L);

        return EventMapper.toFullDto(event, getConfirmedRequests(id), views);
    }

    // Вспомогательные методы
    private void updateEventFields(Event event, String title, String annotation, String description,
                                   LocalDateTime eventDate, Location location, Long categoryId,
                                   Boolean paid, Integer limit, Boolean moderation) {

        if (title != null) event.setTitle(title);
        if (annotation != null) event.setAnnotation(annotation);
        if (description != null) event.setDescription(description);
        if (eventDate != null) event.setEventDate(eventDate);
        if (location != null) event.setLocation(location);
        if (categoryId != null) event.setCategory(getCategory(categoryId));
        if (paid != null) event.setPaid(paid);
        if (limit != null) event.setParticipantLimit(limit);
        if (moderation != null) event.setRequestModeration(moderation);
    }

    private int getConfirmedRequests(Long eventId) {
        return requestRepo.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
    }

    private User getUser(Long id) {
        return userRepo.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

    private Category getCategory(Long id) {
        return categoryRepo.findById(id).orElseThrow(() -> new NotFoundException("Category not found"));
    }

    private void saveStats(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        statsService.saveHit(request.getRequestURI(), ip);
    }
}
