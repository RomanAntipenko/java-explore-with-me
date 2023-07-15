package ru.practicum.ewm.stats.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.stats.EndpointHit;
import ru.practicum.ewm.dto.stats.ViewStats;
import ru.practicum.ewm.stats.errorhandling.IncorrectDateException;
import ru.practicum.ewm.stats.service.StatService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatsController {

    private final StatService statService;

    @GetMapping("/stats")
    public List<ViewStats> getStatsView(@NotNull @RequestParam(name = "start")
                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                        @NotNull @RequestParam(name = "end")
                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                        @RequestParam(name = "unique", defaultValue = "false") Boolean unique,
                                        @RequestParam(name = "uris", required = false) List<String> uris) {
        if (end.isBefore(start)) {
            throw new IncorrectDateException("Переданы некорректные даты");
        }
        log.info("Вызван эндпоинт по получению статистики в stats-server");
        return statService.getStatsView(start, end, unique, uris);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/hit")
    public void createHit(@RequestBody @Valid EndpointHit endpointHit) {
        log.info("Вызван эндпоинт записи статистики в stats-server");
        statService.create(endpointHit);
    }
}
