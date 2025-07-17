package ru.practicum.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;
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

        if (unique && hasUris) {
            return hitRepository.findAllUniqueWithUris(uris, start, end);
        }
        if (unique) {
            return hitRepository.findAllUniqueWithoutUris(start, end);
        }
        if (hasUris) {
            return hitRepository.findAllWithUris(uris, start, end);
        }
        return hitRepository.findAllWithoutUris(start, end);
    }

}

