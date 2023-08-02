package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/comments")
@Validated
public class AdminCommentsController {

    private final CommentService commentService;

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentForAdmin(@PathVariable Long commentId) {
        log.info("Method of deleting comment was caused in AdminCommentsController");
        commentService.deleteCommentForAdmin(commentId);
    }

    @GetMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto getCommentByIdForAdmin(@PathVariable Long commentId) {
        log.info("Method of getting comment by Id was caused in AdminCommentsController");
        return commentService.getCommentByIdForAdmin(commentId);
    }

    @GetMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getUserCommentsForAdmin(@PathVariable Long userId,
                                                    @RequestParam(defaultValue = "COMMENT_DATE_DESC") String sort,
                                                    @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                    @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Method of getting commentList of concrete user was caused in PrivateCommentsController");
        return commentService.geCommentsOfTheUserForAdmin(userId, sort, from, size);
    }
}
