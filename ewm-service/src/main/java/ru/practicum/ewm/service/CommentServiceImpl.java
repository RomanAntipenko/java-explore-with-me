package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.NewCommentDto;
import ru.practicum.ewm.dto.comment.UpdateCommentDto;
import ru.practicum.ewm.enums.EventStatus;
import ru.practicum.ewm.error.ConditionNotMetException;
import ru.practicum.ewm.error.IncorrectRequestException;
import ru.practicum.ewm.error.ObjectNotFoundException;
import ru.practicum.ewm.mapper.CommentMapper;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.CommentRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public CommentDto createComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User commentator = userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("User with id=\"%s\" was not found", userId)));
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Event with id=\"%s\" was not found", eventId)));
        if (event.getState() != EventStatus.PUBLISHED) {
            throw new ConditionNotMetException("Event for commenting must be published");
        }
        Comment comment = CommentMapper.toCommentFromNew(newCommentDto, commentator, event, LocalDateTime.now());
        return CommentMapper.toDtoFromComment(commentRepository.save(comment));
    }

    @Override
    public CommentDto updateComment(Long userId, Long eventId, Long commentId, UpdateCommentDto updateCommentDto) {
        User commentator = userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("User with id=\"%s\" was not found", userId)));
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Event with id=\"%s\" was not found", eventId)));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Comment with id=\"%s\" was not found", commentId)));
        if (!userId.equals(comment.getAuthor().getId())) {
            throw new ConditionNotMetException("Update the comment can only the author of this comment");
        }
        comment.setText(updateCommentDto.getText());
        comment.setRedacted(updateCommentDto.getRedacted());
        return CommentMapper.toDtoFromComment(commentRepository.save(comment));
    }

    @Override
    public void deleteComment(Long userId, Long eventId, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Comment with id=\"%s\" was not found", commentId)));
        User commentator = userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("User with id=\"%s\" was not found", userId)));
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Event with id=\"%s\" was not found", eventId)));
        if (!commentator.getId().equals(comment.getAuthor().getId())) {
            throw new ConditionNotMetException("Delete the comment can only the author of this comment or Admin");
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentDto> getCommentsOfEvent(Long userId, Long eventId, String sort, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException(String.format("User with id=\"%s\" was not found", userId));
        }
        if (!eventRepository.existsById(eventId)) {
            throw new ObjectNotFoundException(String.format("Event with id=\"%s\" was not found", eventId));
        }
        Pageable pageable = getPagination(sort, from, size);
        List<Comment> comments = commentRepository.findAllByEventId(eventId, pageable);
        return comments.stream()
                .map(CommentMapper::toDtoFromComment)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> geCommentsOfTheUserForAdmin(Long userId, String sort, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException(String.format("User with id=\"%s\" was not found", userId));
        }
        Pageable pageable = getPagination(sort, from, size);
        List<Comment> comments = commentRepository.findAllByAuthorId(userId, pageable);
        return comments.stream()
                .map(CommentMapper::toDtoFromComment)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto getCommentByIdForAdmin(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Comment with id=\"%s\" was not found", commentId)));
        return CommentMapper.toDtoFromComment(comment);
    }

    @Override
    public void deleteCommentForAdmin(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new ObjectNotFoundException(String.format("Comment with id=\"%s\" was not found", commentId));
        }
        commentRepository.deleteById(commentId);
    }

    private Pageable getPagination(String sort, Integer from, Integer size) {
        Sort sortForQuery;
        switch (sort.toUpperCase()) {
            case "COMMENT_DATE_ASC":
                sortForQuery = Sort.by("created").ascending();
                break;
            case "COMMENT_DATE_DESC":
                sortForQuery = Sort.by("created").descending();
                break;
            default:
                throw new IncorrectRequestException("No such parameters for sorting");
        }
        return PageRequest.of(from / size, size, sortForQuery);
    }
}
