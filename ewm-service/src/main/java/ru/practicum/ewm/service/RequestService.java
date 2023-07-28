package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.request.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getRequestsForUser(Long userId);

    ParticipationRequestDto createRequestForUser(Long userId, Long eventId);

    ParticipationRequestDto updateRequestForUser(Long userId, Long requestId);
}
