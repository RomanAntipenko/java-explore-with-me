package ru.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.enums.EventStatus;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.Location;
import ru.practicum.ewm.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class EventMapper {
    public Event toEventFromNewEventDto(NewEventDto newEventDto, User user, Category category, Location location,
                                        LocalDateTime created) {
        return Event.builder()
                .eventDate(LocalDateTime.parse(newEventDto.getEventDate(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .category(category)
                .initiator(user)
                .confirmedRequests(0L)
                .createdOn(created)
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .location(location)
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration())
                .publishedOn(null)
                .state(EventStatus.PENDING)
                .title(newEventDto.getTitle())
                .views(0L)
                .build();
    }

    public static EventFullDto toEventFullDtoFromSavedEvent(Event savedEvent) {
        return EventFullDto.builder()
                .id(savedEvent.getId())
                .eventDate(savedEvent.getEventDate())
                .initiator(UserMapper.toShortFromUser(savedEvent.getInitiator()))
                .annotation(savedEvent.getAnnotation())
                .title(savedEvent.getTitle())
                .paid(savedEvent.getPaid())
                .category(CategoryMapper.toDtoFromCategory(savedEvent.getCategory()))
                .confirmedRequests(savedEvent.getConfirmedRequests())
                .createdOn(savedEvent.getCreatedOn())
                .description(savedEvent.getDescription())
                .views(savedEvent.getViews())
                .location(LocationMapper.toDtoFromLocation(savedEvent.getLocation()))
                .participantLimit(savedEvent.getParticipantLimit())
                .state(savedEvent.getState())
                .publishedOn(savedEvent.getPublishedOn())
                .requestModeration(savedEvent.getRequestModeration())
                .build();
    }

    public EventShortDto toShortFromEvent(Event event) {
        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toDtoFromCategory(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .id(event.getId())
                .initiator(UserMapper.toShortFromUser(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }
}
