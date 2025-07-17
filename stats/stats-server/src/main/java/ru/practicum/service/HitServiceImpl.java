package ru.practicum.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;
import ru.practicum.ViewStatsProjection;
import ru.practicum.model.Hit;
import ru.practicum.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HitServiceImpl implements HitService {

    private final HitRepository hitRepository;

    @Override
    @Transactional
    public void saveHit(EndpointHit hitDto) {
        Hit hit = new Hit(null,
                hitDto.getApp(),
                hitDto.getUri(),
                hitDto.getIp(),
                hitDto.getTimestamp());
        hitRepository.save(hit);
    }

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end,
                                    List<String> uris, boolean unique) {
        boolean hasUris = uris != null && !uris.isEmpty();
        List<ViewStatsProjection> projections;

        if (unique && hasUris) {
            projections = hitRepository.findAllUniqueWithUris(uris, start, end);
        } else if (unique) {
            projections = hitRepository.findAllUniqueWithoutUris(start, end);
        } else if (hasUris) {
            projections = hitRepository.findAllWithUris(uris, start, end);
        } else {
            projections = hitRepository.findAllWithoutUris(start, end);
        }

        return projections.stream()
                .map(p -> new ViewStats(p.getApp(), p.getUri(), p.getHits()))
                .toList();
    }

}

