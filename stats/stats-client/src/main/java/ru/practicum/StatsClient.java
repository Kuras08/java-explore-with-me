package ru.practicum;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class StatsClient extends BaseClient {

    public StatsClient(@Value("${stats-server.url}") String serverUrl,
                       RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build()
        );
    }

    public void saveHit(EndpointHit hitDto) {
        post("/hit", hitDto);
    }

    public List<ViewStats> getStats(String start,
                                    String end,
                                    List<String> uris,
                                    boolean unique) {
        Map<String, Object> params = new HashMap<>();
        params.put("start", start);
        params.put("end", end);
        params.put("unique", unique);

        String path = "/stats?start={start}&end={end}&unique={unique}";
        if (uris != null && !uris.isEmpty()) {
            String joined = String.join(",", uris);
            params.put("uris", joined);
            path += "&uris={uris}";
        }

        ResponseEntity<Object> response = get(path, params);
        Object body = response.getBody();

        List<ViewStats> stats = new ArrayList<>();
        if (response.getStatusCode().is2xxSuccessful() && body instanceof List<?> list) {
            ObjectMapper mapper = new ObjectMapper();
            for (Object o : list) {
                stats.add(mapper.convertValue(o, ViewStats.class));
            }
        }
        return stats;
    }
}

