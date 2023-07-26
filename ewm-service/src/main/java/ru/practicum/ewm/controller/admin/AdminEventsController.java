package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.UpdateEventAdminRequest;
import ru.practicum.ewm.enums.EventStatus;
import ru.practicum.ewm.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/events")
@Validated
public class AdminEventsController {

    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getAdminEvents(@RequestParam(required = false) List<Long> users,
                                             @RequestParam(required = false) /*EventStatus states*/ List<EventStatus> states,
                                             @RequestParam(required = false) List<Long> categories,
                                             @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                             @RequestParam(required = false) LocalDateTime rangeStart,
                                             @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                             @RequestParam(required = false) LocalDateTime rangeEnd,
                                             @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                             @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Method of getting events for Admin was caused in AdminEventsController");
        return eventService.getAdminEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateAdminEvent(@PathVariable Long eventId,
                                         @RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("Method of accepting or decline event for Admin was caused in AdminEventsController");
        return eventService.updateAdminEvent(eventId, updateEventAdminRequest);
    }
}
