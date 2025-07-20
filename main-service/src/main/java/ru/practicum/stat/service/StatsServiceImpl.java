package ru.practicum.stat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.stat.client.StatsClient;
import ru.practicum.stat.dto.EndpointHit;
import ru.practicum.stat.dto.ViewStats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private static final String APP_NAME = "ewm-main-service";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatsClient statsClient;

    @Override
    public void saveHit(String uri, String ip) {
        EndpointHit hit = new EndpointHit();
        hit.setApp(APP_NAME);
        hit.setUri(uri);
        hit.setIp(ip);
        hit.setTimestamp(LocalDateTime.now());
        statsClient.saveHit(hit);
        log.info("StatsService: Hit saved for uri {} from ip {}", uri, ip);
    }

    @Override
    public Map<Long, Long> getViews(List<Long> eventIds, boolean unique) {
        if (eventIds == null || eventIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<String> uris = eventIds.stream().map(id -> "/events/" + id).toList();
        LocalDateTime start = LocalDateTime.now().minusYears(20);
        LocalDateTime end = LocalDateTime.now().plusYears(20);

        List<ViewStats> viewStats = statsClient.getStats(start, end, uris, unique);

        Map<Long, Long> views = new HashMap<>();
        for (ViewStats vs : viewStats) {
            try {
                Long eventId = Long.valueOf(vs.getUri().replace("/events/", ""));
                views.put(eventId, vs.getHits());
            } catch (NumberFormatException e) {
                log.warn("Failed to parse eventId from uri {}", vs.getUri());
            }
        }
        return views;
    }
}