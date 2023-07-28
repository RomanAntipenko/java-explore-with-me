package ru.practicum.ewm.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.service.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users")
@Validated
public class PrivateRequestsController {

    private final RequestService requestService;

    @GetMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequestsForUser(@PathVariable Long userId) {
        log.info("Method of getting requests for participation of concrete user was caused " +
                "in PrivateRequestsController");
        return requestService.getRequestsForUser(userId);
    }

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequestForUser(@PathVariable Long userId,
                                                        @RequestParam Long eventId) {
        log.info("Method of creating request for participation from concrete user to concrete event was caused " +
                "in PrivateRequestsController");
        return requestService.createRequestForUser(userId, eventId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto updateRequestForUser(@PathVariable Long userId,
                                                        @PathVariable Long requestId) {
        log.info("Method of canceling request for participation from concrete user was caused " +
                "in PrivateRequestsController");
        return requestService.updateRequestForUser(userId, requestId);
    }
}
