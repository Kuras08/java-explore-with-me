package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentInputDto;
import ru.practicum.comment.dto.CommentOutputDto;

import java.util.List;

public interface CommentService {
    CommentOutputDto addComment(Long userId, Long eventId, CommentInputDto dto);

    List<CommentOutputDto> getUserCommentsForEvent(Long userId, Long eventId);

    CommentOutputDto updateComment(Long userId, Long commentId, CommentInputDto dto);

    void deleteComment(Long userId, Long commentId);

    List<CommentOutputDto> getCommentsForEvent(Long eventId);

    CommentOutputDto getCommentById(Long commentId);

    void deleteAllCommentsByEvent(Long eventId);
}
