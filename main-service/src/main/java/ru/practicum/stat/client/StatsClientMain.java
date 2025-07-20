package ru.practicum.stat.client;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.stat.dto.EndpointHit;
import ru.practicum.stat.dto.ViewStats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatsClientMain {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final RestTemplate restTemplate;
    @Value("${stats-server.url}")
    private String statsServerUrl;

    // Отправка события просмотра
    public void saveHit(EndpointHit hit) {
        String url = statsServerUrl + "/hit";
        restTemplate.postForEntity(url, hit, Void.class);
    }

    // Получение статистики просмотров
    public List<ViewStats> getStats(LocalDateTime start,
                                    LocalDateTime end,
                                    List<String> uris,
                                    boolean unique) {
        String url = statsServerUrl + "/stats?start={start}&end={end}&unique={unique}";
        Map<String, Object> params = new HashMap<>();
        params.put("start", start.format(FORMATTER));
        params.put("end", end.format(FORMATTER));
        params.put("unique", unique);

        if (uris != null && !uris.isEmpty()) {
            String urisParam = String.join(",", uris);
            url += "&uris={uris}";
            params.put("uris", urisParam);
        }

        ResponseEntity<ViewStats[]> response = restTemplate.getForEntity(url, ViewStats[].class, params);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return Arrays.asList(response.getBody());
        }
        return Collections.emptyList();
    }
}

