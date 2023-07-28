package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.enums.EventStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size);

    EventFullDto getEventOfUser(Long userId, Long eventId);

    EventFullDto updateEventOfUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<ParticipationRequestDto> getRequestsOnEventOfUser(Long userId, Long eventId);

    EventRequestStatusUpdateResult getResponseOnEventOfUser(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

    List<EventFullDto> getAdminEvents(List<Long> users, List<EventStatus> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    EventFullDto updateAdminEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size, HttpServletRequest httpServletRequest);

    EventFullDto getEventById(Long eventId, HttpServletRequest httpServletRequest);
}
