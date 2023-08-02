package ru.practicum.ewm.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.NewCommentDto;
import ru.practicum.ewm.dto.comment.UpdateCommentDto;
import ru.practicum.ewm.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/users")
public class PrivateCommentsController {

    private final CommentService commentService;

    @PostMapping("/{userId}/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("Method of creating comment was caused in PrivateCommentsController");
        return commentService.createComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("/{userId}/events/{eventId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateComment(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @PathVariable Long commentId,
                                    @Valid @RequestBody UpdateCommentDto updateCommentDto) {
        log.info("Method of updating comment was caused in PrivateCommentsController");
        return commentService.updateComment(userId, eventId, commentId, updateCommentDto);
    }

    @DeleteMapping("/{userId}/events/{eventId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long userId,
                              @PathVariable Long eventId,
                              @PathVariable Long commentId) {
        log.info("Method of deleting comment was caused in PrivateCommentsController");
        commentService.deleteComment(userId, eventId, commentId);
    }

    @GetMapping("/{userId}/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getCommentsOfEventForAllUsers(@PathVariable Long userId,
                                                          @PathVariable Long eventId,
                                                          @RequestParam(defaultValue = "COMMENT_DATE_DESC") String sort,
                                                          @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                          @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Method of getting commentList of event was caused in PrivateCommentsController");
        return commentService.getCommentsOfEvent(userId, eventId, sort, from, size);
    }
}
