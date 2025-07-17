package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByRequesterId(Long userId);

    List<Request> findByEventId(Long eventId);

    List<Request> findByEventIdAndIdIn(Long eventId, List<Long> ids);

    int countByEventIdAndStatus(Long eventId, RequestStatus status);

    Optional<Request> findByIdAndRequesterId(Long requestId, Long requesterId);
}

