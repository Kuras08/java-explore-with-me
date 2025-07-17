package ru.practicum.mapper;

import ru.practicum.EndpointHit;
import ru.practicum.model.Hit;

public class HitMapper {
    public static Hit toEntity(EndpointHit dto) {
        return new Hit(
                null,
                dto.getApp(),
                dto.getUri(),
                dto.getIp(),
                dto.getTimestamp()
        );
    }
}

