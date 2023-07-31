package ru.practicum.ewm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByAuthorId(Long authorId, Pageable pageable);

    List<Comment> findAllByEventId(Long eventId, Pageable pageable);

    List<Comment> findAllByEventIdAndAuthorId(Long eventId, Long authorId, Pageable pageable);
}
