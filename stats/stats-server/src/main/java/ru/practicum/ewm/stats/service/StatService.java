package ru.practicum.ewm.stats.service;

import ru.practicum.ewm.dto.stats.EndpointHit;
import ru.practicum.ewm.dto.stats.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {
    List<ViewStats> getStatsView(LocalDateTime start, LocalDateTime end, Boolean unique, List<String> uris);

    void create(EndpointHit endpointHit);
}
