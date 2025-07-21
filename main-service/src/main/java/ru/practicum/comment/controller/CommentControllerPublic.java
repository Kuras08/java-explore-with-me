package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comment.dto.CommentOutputDto;
import ru.practicum.comment.service.CommentService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
public class CommentControllerPublic {
    private final CommentService commentService;

    @GetMapping("/events/{eventId}/comments")
    public List<CommentOutputDto> getCommentsForEvent(@PathVariable Long eventId) {
        return commentService.getCommentsForEvent(eventId);
    }

    @GetMapping("/comments/{commentId}")
    public CommentOutputDto getCommentById(@PathVariable Long commentId) {
        return commentService.getCommentById(commentId);
    }
}

