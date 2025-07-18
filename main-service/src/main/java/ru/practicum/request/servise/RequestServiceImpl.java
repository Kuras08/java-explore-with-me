package ru.practicum.request.servise;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepo;
    private final EventRepository eventRepo;
    private final UserRepository userRepo;

    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        User user = getUser(userId);
        Event event = getEvent(eventId);

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Initiator cannot request to participate in own event.");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Event must be published to accept requests.");
        }

        boolean exists = requestRepo.findByEventIdAndIdIn(eventId, List.of(userId)).size() > 0;
        if (exists) {
            throw new ConflictException("Duplicate request is not allowed.");
        }

        int confirmed = requestRepo.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        if (event.getParticipantLimit() > 0 && confirmed >= event.getParticipantLimit()) {
            throw new ConflictException("Participant limit has been reached.");
        }

        Request request = new Request();
        request.setRequester(user);
        request.setEvent(event);
        request.setCreated(LocalDateTime.now());
        request.setStatus(
                !event.getRequestModeration() || event.getParticipantLimit() == 0
                        ? RequestStatus.CONFIRMED
                        : RequestStatus.PENDING
        );

        return toDto(requestRepo.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        return requestRepo.findByRequesterId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        Request request = requestRepo.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundException("Request not found"));

        request.setStatus(RequestStatus.CANCELED);
        return toDto(requestRepo.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        Event event = getEvent(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("User is not initiator");
        }

        return requestRepo.findByEventId(eventId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult updateEventRequests(Long userId, Long eventId, EventRequestStatusUpdateRequest dto) {
        Event event = getEvent(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("User is not initiator");
        }

        List<Request> requests = requestRepo.findByEventIdAndIdIn(eventId, dto.getRequestIds());
        List<ParticipationRequestDto> confirmed = new ArrayList<>();
        List<ParticipationRequestDto> rejected = new ArrayList<>();

        int confirmedCount = requestRepo.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        boolean limitReached = event.getParticipantLimit() > 0 &&
                confirmedCount >= event.getParticipantLimit();

        for (Request r : requests) {
            if (!r.getStatus().equals(RequestStatus.PENDING)) {
                throw new ConflictException("Only pending requests can be modified.");
            }

            if (dto.getStatus().equals("CONFIRMED")) {
                if (limitReached) {
                    r.setStatus(RequestStatus.REJECTED);
                    rejected.add(toDto(r));
                } else {
                    r.setStatus(RequestStatus.CONFIRMED);
                    confirmed.add(toDto(r));
                    confirmedCount++;
                    if (confirmedCount >= event.getParticipantLimit()) {
                        limitReached = true;
                    }
                }
            } else if (dto.getStatus().equals("REJECTED")) {
                r.setStatus(RequestStatus.REJECTED);
                rejected.add(toDto(r));
            }
        }

        requestRepo.saveAll(requests);
        return new EventRequestStatusUpdateResult(confirmed, rejected);
    }

    private ParticipationRequestDto toDto(Request r) {
        return new ParticipationRequestDto(
                r.getId(),
                r.getEvent().getId(),
                r.getRequester().getId(),
                r.getStatus().name(),
                r.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }

    private User getUser(Long id) {
        return userRepo.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

    private Event getEvent(Long id) {
        return eventRepo.findById(id).orElseThrow(() -> new NotFoundException("Event not found"));
    }
}

