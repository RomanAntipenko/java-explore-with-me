package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.client.stats.StatsClient;
import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.dto.stats.EndpointHit;
import ru.practicum.ewm.enums.EventRequestStatus;
import ru.practicum.ewm.enums.EventStatus;
import ru.practicum.ewm.enums.StateAction;
import ru.practicum.ewm.error.ConditionNotMetException;
import ru.practicum.ewm.error.FieldUniqueException;
import ru.practicum.ewm.error.IncorrectRequestException;
import ru.practicum.ewm.error.ObjectNotFoundException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.mapper.RequestMapper;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.repository.*;

import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        LocalDateTime eventDate = LocalDateTime.parse(newEventDto.getEventDate(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2L))) {
            throw new IncorrectRequestException(String.format("Field: eventDate. " +
                    "Error: must have the date witch are not already was. Value: \"%s\"", eventDate));
        }
        User creatorOfEvent = userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("User with id=\"%s\" was not found", userId)));
        Category categoryOfEvent = categoryRepository.findById(newEventDto.getCategory()).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Category with id=\"%s\" was not found", newEventDto.getCategory())));
        Location locationOfEvent = locationRepository.save(Location.builder()
                .lon(newEventDto.getLocation().getLon())
                .lat(newEventDto.getLocation().getLat())
                .build());
        LocalDateTime created = LocalDateTime.now();
        Event event = EventMapper.toEventFromNewEventDto(newEventDto, creatorOfEvent, categoryOfEvent, locationOfEvent,
                created);
        try {
            Event savedEvent = eventRepository.save(event);
            return EventMapper.toEventFullDtoFromSavedEvent(savedEvent);
        } catch (DataIntegrityViolationException e) {
            throw new FieldUniqueException(e.getMessage());
        }
    }

    @Override
    public List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException(String.format("User with id=\"%s\" was not found", userId));
        }
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Event> eventList = eventRepository.findAllByInitiatorId(userId, pageRequest);
        return eventList.stream()
                .map(EventMapper::toShortFromEvent)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventOfUser(Long userId, Long eventId) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException(String.format("User with id=\"%s\" was not found", userId));
        }
        if (!eventRepository.existsById(eventId)) {
            throw new ObjectNotFoundException(String.format("Event with id=\"%s\" was not found", eventId));
        }
        Event event = eventRepository.findByInitiatorIdAndId(userId, eventId);
        return EventMapper.toEventFullDtoFromSavedEvent(event);
    }

    @Override
    public EventFullDto updateEventOfUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        LocalDateTime eventDate;

        User user = userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("User with id=\"%s\" was not found", userId)));
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Event with id=\"%s\" was not found", eventId)));

        if (updateEventUserRequest.getEventDate() != null) {
            eventDate = LocalDateTime.parse(updateEventUserRequest.getEventDate(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            if (eventDate.isBefore(LocalDateTime.now().plusHours(2L))) {
                /*throw new ConditionValidationException(String.format("Field: eventDate. " +
                        "Error: must have the date witch are not already was plus 2 hours. Value: \"%s\"", eventDate));*/
                throw new IncorrectRequestException(String.format("Field: eventDate. " +
                        "Error: must have the date witch are not already was plus 2 hours. Value: \"%s\"", eventDate));
            }
            event.setEventDate(eventDate);
        }

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConditionNotMetException("Only initiator can update events like this");
        }

        if (updateEventUserRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventUserRequest.getCategory()).orElseThrow(() ->
                    new ObjectNotFoundException(String.format("Category with id=\"%s\" was not found", updateEventUserRequest.getCategory())));
            event.setCategory(category);
        }

        if (!event.getState().equals(EventStatus.CANCELED) && !event.getState().equals(EventStatus.PENDING)) {
            throw new ConditionNotMetException("Only pending or canceled events can be changed");
        }

        if (updateEventUserRequest.getStateAction() != null) {
            if (updateEventUserRequest.getStateAction().equals(StateAction.CANCEL_REVIEW)) {
                event.setState(EventStatus.CANCELED);
            } else {
                event.setState(EventStatus.PENDING);
            }
        }

        if (updateEventUserRequest.getLocation() != null) {
            event.getLocation().setLat(updateEventUserRequest.getLocation().getLat());
            event.getLocation().setLon(updateEventUserRequest.getLocation().getLon());
        }

        event.setAnnotation(Optional.ofNullable(updateEventUserRequest.getAnnotation()).orElse(event.getAnnotation()));
        event.setDescription(Optional.ofNullable(updateEventUserRequest.getDescription()).orElse(event.getDescription()));
        event.setPaid(Optional.ofNullable(updateEventUserRequest.getPaid()).orElse(event.getPaid()));
        event.setTitle(Optional.ofNullable(updateEventUserRequest.getTitle()).orElse(event.getTitle()));
        event.setRequestModeration(Optional.ofNullable(updateEventUserRequest.getRequestModeration()).orElse(event.getRequestModeration()));
        event.setParticipantLimit(Optional.ofNullable(updateEventUserRequest.getParticipantLimit()).orElse(event.getParticipantLimit()));

        try {
            Event savedEvent = eventRepository.save(event);
            return EventMapper.toEventFullDtoFromSavedEvent(savedEvent);
        } catch (DataIntegrityViolationException e) {
            throw new FieldUniqueException(e.getMessage());
        }
    }

    @Override
    public List<ParticipationRequestDto> getRequestsOnEventOfUser(Long userId, Long eventId) {
        if (!eventRepository.existsByInitiatorIdAndId(userId, eventId)) { //
            throw new ObjectNotFoundException(String.format("Event with id=\"%s\" was not found", eventId));
        }
        return requestRepository.findAllByEventId(eventId).stream()
                .map(RequestMapper::toDtoFromRequest)
                .collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult getResponseOnEventOfUser(Long userId, Long eventId,
                                                                   EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        Event event = eventRepository.findByInitiatorIdAndId(userId, eventId);
        if (event == null) {
            throw new ObjectNotFoundException(String.format("Event with id=\"%s\" was not found", eventId));
        }
        List<Request> requests = requestRepository.findAllByEventIdAndIdIn(eventId, eventRequestStatusUpdateRequest.getRequestIds());
        EventRequestStatusUpdateResult result = EventRequestStatusUpdateResult.builder()
                .confirmedRequests(new ArrayList<>())
                .rejectedRequests(new ArrayList<>())
                .build();
        Long limit = event.getParticipantLimit();
        if (!event.getRequestModeration() && limit == 0) {
            result.setConfirmedRequests(requests.stream()
                    .map(RequestMapper::toDtoFromRequest)
                    .collect(Collectors.toList()));
            return result;
        }
        if (event.getConfirmedRequests() >= limit) {
            throw new ConditionNotMetException("Number of participation are limited");
        }

        for (Request request : requests) {
            Long confirmed = event.getConfirmedRequests();
            if (confirmed >= limit) {
                request.setStatus(EventRequestStatus.REJECTED);
                result.getRejectedRequests().add(RequestMapper.toDtoFromRequest(request));
                requestRepository.save(request);
            }
            if (!request.getStatus().equals(EventRequestStatus.PENDING)) {
                throw new ConditionNotMetException("Impossible to change request status which is not in PENDING");
            }
            if (eventRequestStatusUpdateRequest.getStatus().equals(EventRequestStatus.CONFIRMED)) {
                request.setStatus(EventRequestStatus.CONFIRMED);
                requestRepository.save(request);
                event.setConfirmedRequests(event.getConfirmedRequests() + 1L);
                event = eventRepository.save(event);
                result.getConfirmedRequests().add(RequestMapper.toDtoFromRequest(request));
            } else {
                request.setStatus(EventRequestStatus.REJECTED);
                requestRepository.save(request);
                result.getRejectedRequests().add(RequestMapper.toDtoFromRequest(request));
            }
        }
        return result;
    }

    @Override
    public List<EventFullDto> getAdminEvents(List<Long> users, List<EventStatus> states, List<Long> categories,
                                             LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);

        Specification<Event> specification = (Root<Event> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (users != null && !users.isEmpty()) {
                predicates.add(root.join("initiator", JoinType.INNER).get("id").in(users));
            }
            if (states != null && !states.isEmpty()) {
                predicates.add(root.get("state").in(states));
            }
            if (categories != null && !categories.isEmpty()) {
                predicates.add(root.join("category", JoinType.INNER).get("id").in(categories));
            }
            if (rangeStart != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
            }
            if (rangeEnd != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return eventRepository.findAll(specification, pageable).toList().stream()
                .map(EventMapper::toEventFullDtoFromSavedEvent)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateAdminEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        LocalDateTime eventDate;
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Event with id=\"%s\" was not found", eventId)));

        if (updateEventAdminRequest.getEventDate() != null) {
            eventDate = LocalDateTime.parse(updateEventAdminRequest.getEventDate(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            if (eventDate.isBefore(LocalDateTime.now().plusHours(1L))) {
                throw new IncorrectRequestException(String.format("Field: eventDate. " +
                        "Error: must have the date witch are not already was plus 1 hour. Value: \"%s\"", eventDate));
            }
            event.setEventDate(eventDate);
        }

        if (updateEventAdminRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventAdminRequest.getCategory()).orElseThrow(() ->
                    new ObjectNotFoundException(String.format("Category with id=\"%s\" was not found",
                            updateEventAdminRequest.getCategory())));
            event.setCategory(category);
        }

        if (!event.getState().equals(EventStatus.PENDING)) {
            throw new ConditionNotMetException("Only pending events can be approved or rejected by Admin");
        }
        if (updateEventAdminRequest.getStateAction() != null) {
            if (updateEventAdminRequest.getStateAction() == StateAction.PUBLISH_EVENT) {
                event.setState(EventStatus.PUBLISHED);
            } else {
                event.setState(EventStatus.CANCELED);
            }
        }

        if (updateEventAdminRequest.getLocation() != null) {
            event.getLocation().setLat(updateEventAdminRequest.getLocation().getLat());
            event.getLocation().setLon(updateEventAdminRequest.getLocation().getLon());
        }
        event.setAnnotation(Optional.ofNullable(updateEventAdminRequest.getAnnotation()).orElse(event.getAnnotation()));
        event.setDescription(Optional.ofNullable(updateEventAdminRequest.getDescription()).orElse(event.getDescription()));
        event.setPaid(Optional.ofNullable(updateEventAdminRequest.getPaid()).orElse(event.getPaid()));
        event.setTitle(Optional.ofNullable(updateEventAdminRequest.getTitle()).orElse(event.getTitle()));
        event.setRequestModeration(Optional.ofNullable(updateEventAdminRequest.getRequestModeration()).orElse(event.getRequestModeration()));
        event.setParticipantLimit(Optional.ofNullable(updateEventAdminRequest.getParticipantLimit()).orElse(event.getParticipantLimit()));

        try {
            Event savedEvent = eventRepository.save(event);
            return EventMapper.toEventFullDtoFromSavedEvent(savedEvent);
        } catch (DataIntegrityViolationException e) {
            throw new FieldUniqueException(e.getMessage());
        }
    }

    @Override
    public List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid, LocalDateTime
            rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size,
                                         HttpServletRequest httpServletRequest) {
        if (rangeStart == null && rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(100);
            rangeStart = LocalDateTime.now();
        } else if (rangeEnd != null && rangeStart != null && rangeEnd.isBefore(rangeStart)) {
            throw new IncorrectRequestException("Impossible to use endDate which is before startDate of event");
        }
        LocalDateTime startSearch = rangeStart;
        LocalDateTime endSearch = rangeEnd;
        Pageable pageable;
        Sort sort1;
        if (sort.equalsIgnoreCase("VIEWS")) {
            sort1 = Sort.by("views").descending();
        } else {
            sort1 = Sort.by("eventDate").descending();
        }
        pageable = PageRequest.of(from / size, size, sort1);
        Specification<Event> specification = (Root<Event> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("state"), EventStatus.PUBLISHED));
            if (text != null) {
                predicates.add(cb.or(cb.like(cb.lower(root.get("description")), "%" + text.toLowerCase() + "%"),
                        cb.like(cb.lower(root.get("annotation")), "%" + text.toLowerCase() + "%")));
            }
            if (categories != null && !categories.isEmpty()) {
                predicates.add(root.join("category", JoinType.INNER).get("id").in(categories));
            }
            if (paid != null) {
                predicates.add(cb.equal(root.get("paid"), paid));
            }
            if (startSearch != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("eventDate"), startSearch));
            }
            if (endSearch != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("eventDate"), endSearch));
            }
            if (onlyAvailable != null && onlyAvailable) {
                predicates.add(cb.greaterThan(root.get("participantLimit"), root.get("confirmedRequests")));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };

        Page<Event> eventList = eventRepository.findAll(specification, pageable)/*.toList()*/;
        statsClient.createHit(EndpointHit.builder()
                .uri(httpServletRequest.getRequestURI())
                .ip(httpServletRequest.getRemoteAddr())
                .app("ewm-service")
                .timestamp(LocalDateTime.now()) //
                .build());
        return eventList.stream()
                .map(EventMapper::toShortFromEvent)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventById(Long eventId, HttpServletRequest httpServletRequest) {
        Event event = eventRepository.findFirstByIdAndState(eventId, EventStatus.PUBLISHED);
        if (event == null) {
            throw new ObjectNotFoundException(String.format("Event with id=\"%s\" was not found", eventId));
        }
        Long views = (long) statsClient.getViewStats(LocalDateTime.now().minusYears(100), LocalDateTime.now().plusYears(100),
                true, List.of(httpServletRequest.getRequestURI())).size();
        statsClient.createHit(EndpointHit.builder()
                .uri(httpServletRequest.getRequestURI())
                .ip(httpServletRequest.getRemoteAddr())
                .app("ewm-service")
                .timestamp(LocalDateTime.now()) //
                .build());
        Long newViews = (long) statsClient.getViewStats(LocalDateTime.now().minusYears(100), LocalDateTime.now().plusYears(100),
                true, List.of(httpServletRequest.getRequestURI())).size();
        if (newViews > views) {
            event.setViews(newViews);
            eventRepository.save(event);
        }
        return EventMapper.toEventFullDtoFromSavedEvent(event);
    }
}
