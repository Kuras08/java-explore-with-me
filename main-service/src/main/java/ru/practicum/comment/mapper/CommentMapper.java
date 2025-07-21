package ru.practicum.comment.mapper;


import ru.practicum.comment.dto.CommentInputDto;
import ru.practicum.comment.dto.CommentOutputDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {

    public static Comment toEntity(CommentInputDto dto, User author, Event event) {
        return Comment.builder()
                .text(dto.getText())
                .author(author)
                .event(event)
                .created(LocalDateTime.now())
                .deleted(false)
                .build();
    }

    public static CommentOutputDto toOutputDto(Comment comment) {
        return new CommentOutputDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(), // выводим только имя автора
                EventMapper.toShortDto(comment.getEvent(), 0, 0),
                comment.getCreated()
        );
    }

    public static List<CommentOutputDto> toOutputDtoList(List<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::toOutputDto)
                .collect(Collectors.toList());
    }
}

