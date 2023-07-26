package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.enums.EventRequestStatus;
import ru.practicum.ewm.enums.EventStatus;
import ru.practicum.ewm.error.ConditionNotMetException;
import ru.practicum.ewm.error.ConditionValidationException;
import ru.practicum.ewm.error.FieldUniqueException;
import ru.practicum.ewm.error.ObjectNotFoundException;
import ru.practicum.ewm.mapper.RequestMapper;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.Request;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public List<ParticipationRequestDto> getRequestsForUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException(String.format("User with id=\"%s\" was not found", userId));
        }
        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        return requests.stream()
                .map(RequestMapper::toDtoFromRequest)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto createRequestForUser(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Event with id=\"%s\" was not found", eventId)));
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("User with id=\"%s\" was not found", userId)));
        if (!event.getState().equals(EventStatus.PUBLISHED)) {
            throw new ConditionNotMetException("Impossible to create request to unpublished event");
        }
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConditionNotMetException("Impossible to create request from event initiator");
        }
        if (event.getConfirmedRequests() >= event.getParticipantLimit() && event.getParticipantLimit() != 0) {
            throw new ConditionNotMetException("Was reached participation limit in event");
        }
        Request request;

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request = RequestMapper.toNewRequest(user, event, LocalDateTime.now(), EventRequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1L);
            eventRepository.save(event);
        } else {
            request = RequestMapper.toNewRequest(user, event, LocalDateTime.now(), EventRequestStatus.PENDING);
        }
        try {
            return RequestMapper.toDtoFromRequest(requestRepository.save(request));
        } catch (DataIntegrityViolationException e) {
            throw new FieldUniqueException(e.getMessage());
        }
    }

    @Override
    public ParticipationRequestDto updateRequestForUser(Long userId, Long requestId) {
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId).orElseThrow(() ->
                new ConditionValidationException(String.format(
                        "There are not such Request with userId = \"%s\" and requestId = \"%s\"", userId, requestId)));
        request.setStatus(EventRequestStatus.CANCELED);
        return RequestMapper.toDtoFromRequest(requestRepository.save(request));
    }
}
