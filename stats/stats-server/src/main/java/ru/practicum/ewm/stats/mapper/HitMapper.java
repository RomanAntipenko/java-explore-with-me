package ru.practicum.ewm.stats.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.stats.EndpointHit;
import ru.practicum.ewm.stats.model.Hit;

@UtilityClass
public class HitMapper {
    public EndpointHit toEndpointHit(Hit hit) {
        return EndpointHit.builder()
                .app(hit.getApp())
                .ip(hit.getIp())
                .id(hit.getId())
                .timestamp(hit.getCreated())
                .uri(hit.getUri())
                .build();

    }

    public Hit toHit(EndpointHit endpointHit) {
        return Hit.builder()
                .app(endpointHit.getApp())
                .ip(endpointHit.getIp())
                .uri(endpointHit.getUri())
                .created(endpointHit.getTimestamp())
                .build();
    }
}
