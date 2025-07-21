package ru.practicum.comment.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.CommentInputDto;
import ru.practicum.comment.dto.CommentOutputDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CommentOutputDto addComment(Long userId, Long eventId, CommentInputDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found: " + eventId));

        Comment comment = CommentMapper.toEntity(dto, user, event);
        commentRepository.save(comment);
        return CommentMapper.toOutputDto(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentOutputDto> getUserCommentsForEvent(Long userId, Long eventId) {
        return CommentMapper.toOutputDtoList(
                commentRepository.findByAuthorIdAndEventIdAndDeletedFalse(userId, eventId)
        );
    }

    @Override
    @Transactional
    public CommentOutputDto updateComment(Long userId, Long commentId, CommentInputDto dto) {
        Comment comment = commentRepository.findByIdAndAuthorIdAndDeletedFalse(commentId, userId)
                .orElseThrow(() -> new NotFoundException("Comment not found or not your comment"));

        comment.setText(dto.getText());
        comment.setUpdated(java.time.LocalDateTime.now());
        commentRepository.save(comment);

        return CommentMapper.toOutputDto(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findByIdAndAuthorIdAndDeletedFalse(commentId, userId)
                .orElseThrow(() -> new NotFoundException("Comment not found or not your comment"));
        comment.setDeleted(true);
        commentRepository.save(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentOutputDto> getCommentsForEvent(Long eventId) {
        return CommentMapper.toOutputDtoList(
                commentRepository.findByEventIdAndDeletedFalse(eventId)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CommentOutputDto getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));
        if (comment.isDeleted()) throw new NotFoundException("Comment deleted");
        return CommentMapper.toOutputDto(comment);
    }

    @Override
    @Transactional // это массовый update
    public void deleteAllCommentsByEvent(Long eventId) {
        List<Comment> comments = commentRepository.findByEventIdAndDeletedFalse(eventId);
        for (Comment comment : comments) {
            comment.setDeleted(true);
            commentRepository.save(comment);
        }
    }
}
