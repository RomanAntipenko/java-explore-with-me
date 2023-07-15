package ru.practicum.ewm.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.stats.EndpointHit;
import ru.practicum.ewm.dto.stats.ViewStats;
import ru.practicum.ewm.stats.mapper.HitMapper;
import ru.practicum.ewm.stats.model.Hit;
import ru.practicum.ewm.stats.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {
    private final StatRepository statRepository;

    @Override
    public List<ViewStats> getStatsView(LocalDateTime start, LocalDateTime end, Boolean unique,
                                        List<String> uris) {
        List<Hit> list;
        if (uris == null || uris.isEmpty()) {
            list = statRepository.findStatsWithoutUris(start, end);
        } else {
            list = statRepository.findStatsWithUris(start, end, uris);
        }
        return convert(list, unique);
    }

    public List<ViewStats> convert(List<Hit> list, Boolean unique) {
        Map<String, List<Hit>> stringListEndpointHitMap;
        List<ViewStats> viewStatsList;
        if (unique) {
            stringListEndpointHitMap = list.stream()
                    .collect(Collectors.groupingBy(Hit::getUri));
            viewStatsList = stringListEndpointHitMap.keySet().stream()
                    .map(key -> ViewStats.builder()
                            .uri(key)
                            .app(stringListEndpointHitMap.get(key).get(0).getApp())
                            .hits(stringListEndpointHitMap.get(key).stream()
                                    .map(Hit::getIp)
                                    .distinct()
                                    .count())
                            .build())
                    .collect(Collectors.toList());
            return viewStatsList;
        } else {
            stringListEndpointHitMap = list.stream()
                    .collect(Collectors.groupingBy(Hit::getUri));
            viewStatsList = stringListEndpointHitMap.keySet().stream()
                    .map(key -> ViewStats.builder()
                            .uri(key)
                            .app(stringListEndpointHitMap.get(key).get(0).getApp())
                            .hits((long) stringListEndpointHitMap.get(key).size())
                            .build())
                    .collect(Collectors.toList());
            return viewStatsList;
        }
    }

    @Override
    public void create(EndpointHit endpointHit) {
        statRepository.save(HitMapper.toHit(endpointHit));
    }
}
