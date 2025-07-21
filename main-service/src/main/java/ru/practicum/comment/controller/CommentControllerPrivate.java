package ru.practicum.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentInputDto;
import ru.practicum.comment.dto.CommentOutputDto;
import ru.practicum.comment.service.CommentService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}")
public class CommentControllerPrivate {
    private final CommentService commentService;

    @GetMapping("/events/{eventId}/comments")
    public List<CommentOutputDto> getUserCommentsForEvent(@PathVariable Long userId,
                                                          @PathVariable Long eventId) {
        return commentService.getUserCommentsForEvent(userId, eventId);
    }

    @PostMapping("/events/{eventId}/comments")
    public CommentOutputDto addComment(@PathVariable Long userId,
                                       @PathVariable Long eventId,
                                       @RequestBody @Valid CommentInputDto dto) {
        return commentService.addComment(userId, eventId, dto);
    }

    @PatchMapping("/comments/{commentId}")
    public CommentOutputDto updateComment(@PathVariable Long userId,
                                          @PathVariable Long commentId,
                                          @RequestBody @Valid CommentInputDto dto) {
        return commentService.updateComment(userId, commentId, dto);
    }

    @DeleteMapping("/comments/{commentId}")
    public void deleteComment(@PathVariable Long userId,
                              @PathVariable Long commentId) {
        commentService.deleteComment(userId, commentId);
    }
}


