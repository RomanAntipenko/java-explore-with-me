package ru.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.NewCommentDto;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {
    public Comment toCommentFromNew(NewCommentDto newCommentDto, User author, Event event, LocalDateTime created) {
        return Comment.builder()
                .text(newCommentDto.getText())
                .author(author)
                .event(event)
                .redacted(newCommentDto.getRedacted())
                .created(created)
                .build();
    }

    public CommentDto toDtoFromComment(Comment comment) {
        return CommentDto.builder()
                .text(comment.getText())
                .author(comment.getAuthor().getId())
                .event(comment.getEvent().getId())
                .created(comment.getCreated())
                .redacted(comment.getRedacted())
                .id(comment.getId())
                .build();
    }
}
