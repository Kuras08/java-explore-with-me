package ru.practicum.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.comment.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByEventIdAndDeletedFalse(Long eventId);

    List<Comment> findByAuthorIdAndEventIdAndDeletedFalse(Long authorId, Long eventId);

    Optional<Comment> findByIdAndAuthorIdAndDeletedFalse(Long commentId, Long authorId);

    List<Comment> findByAuthorIdAndDeletedFalse(Long authorId);

    void deleteAllByEventId(Long eventId);
}