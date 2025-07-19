package ru.practicum.event.mapper;

import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

public class EventMapper {


    public static EventFullDto toFullDto(Event event, int confirmedRequests, long views) {
        return EventFullDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .category(event.getCategory() != null ? CategoryMapper.toDto(event.getCategory()) : null)
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(event.getInitiator() != null ? UserMapper.toShortDto(event.getInitiator()) : null)
                .location(event.getLocation())
                .paid(event.getPaid()) // <--- Важно, если Boolean!
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration()) // <--- Boolean!
                .state(event.getState() != null ? event.getState().name() : null)
                .views(views)
                .confirmedRequests(confirmedRequests)
                .build();
    }

    public static EventShortDto toShortDto(Event event) {
        return toShortDto(event, 0, event.getViews());
    }

    public static EventShortDto toShortDto(Event event, int confirmedRequests) {
        return toShortDto(event, confirmedRequests, event.getViews());
    }

    public static EventShortDto toShortDto(Event event, int confirmedRequests, long views) {
        return EventShortDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .views(views)
                .confirmedRequests(confirmedRequests)
                .build();
    }

    public static Event toEntity(NewEventDto dto, User initiator, Category category) {
        Event event = new Event();
        event.setTitle(dto.getTitle());
        event.setAnnotation(dto.getAnnotation());
        event.setDescription(dto.getDescription());
        event.setEventDate(dto.getEventDate());
        event.setLocation(dto.getLocation());
        event.setCategory(category);
        event.setInitiator(initiator);
        event.setPaid(dto.isPaid());
        event.setParticipantLimit(dto.getParticipantLimit());
        event.setRequestModeration(dto.isRequestModeration());
        event.setState(EventState.PENDING);
        event.setCreatedOn(LocalDateTime.now());
        return event;
    }
}


