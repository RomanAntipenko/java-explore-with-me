package ru.practicum.ewm.client.stats;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.ewm.dto.stats.EndpointHit;
import ru.practicum.ewm.dto.stats.ViewStats;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class StatsClient {
    private final String serverUrl;
    private final RestTemplate rest;

    public StatsClient(@Value("${stats.server.url}") String serverUrl) {
        this.rest = new RestTemplate();
        this.serverUrl = serverUrl;
    }

    public List<ViewStats> getViewStats(LocalDateTime start, LocalDateTime end, Boolean unique, List<String> uris) {
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "unique", unique,
                "uris", uris
        );

        ResponseEntity<List<ViewStats>> responseEntity = rest.exchange(
                "/stats?start={start}&end={end}&uris={uris}&unique={unique}", HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                }, parameters);
        return responseEntity.getBody();
    }

    public void createHit(EndpointHit endpointHit) {
        rest.exchange("/hit", HttpMethod.POST, new HttpEntity<>(endpointHit), Object.class);
    }
}