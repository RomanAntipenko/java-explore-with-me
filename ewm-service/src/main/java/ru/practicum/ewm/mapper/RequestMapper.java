package ru.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.enums.EventRequestStatus;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.Request;
import ru.practicum.ewm.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class RequestMapper {
    public Request toNewRequest(User user, Event event, LocalDateTime created, EventRequestStatus eventRequestStatus) {
        return Request.builder()
                .requester(user)
                .status(eventRequestStatus)
                .event(event)
                .created(created.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    public ParticipationRequestDto toDtoFromRequest(Request request) {
        return ParticipationRequestDto.builder()
                .requester(request.getId())
                .event(request.getEvent().getId())
                .id(request.getId())
                .created(request.getCreated())
                .status(request.getStatus())
                .build();
    }
}
