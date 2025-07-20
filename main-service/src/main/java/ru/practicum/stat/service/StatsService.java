package ru.practicum.stat.service;

import java.util.List;
import java.util.Map;

public interface StatsService {

    void saveHit(String uri, String ip);

    Map<Long, Long> getViews(List<Long> eventIds, boolean unique);
}