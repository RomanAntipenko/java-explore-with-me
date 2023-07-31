package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.NewCommentDto;
import ru.practicum.ewm.dto.comment.UpdateCommentDto;

import java.util.List;

public interface CommentService {
    CommentDto createComment(Long userId, Long eventId, NewCommentDto newCommentDto);

    CommentDto updateComment(Long userId, Long eventId, Long commentId, UpdateCommentDto updateCommentDto);

    void deleteComment(Long userId, Long eventId, Long commentId);

    List<CommentDto> getCommentsOfEvent(Long userId, Long eventId, String sort, Integer from, Integer size);

    void deleteCommentForAdmin(Long commentId);

    CommentDto getCommentByIdForAdmin(Long commentId);

    List<CommentDto> geCommentsOfTheUserForAdmin(Long userId, String sort, Integer from, Integer size);


}
