package ru.practicum.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ViewStats;
import ru.practicum.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HitRepository extends JpaRepository<Hit, Long> {

    @Query("""
            SELECT new ru.practicum.ViewStats(s.app, s.uri, COUNT(s.ip))
            FROM Hit s
            WHERE s.timestamp BETWEEN :start AND :end
              AND s.uri IN :uris
            GROUP BY s.app, s.uri
            ORDER BY COUNT(s.ip) DESC
            """)
    List<ViewStats> findAllWithUris(@Param("uris") List<String> uris,
                                    @Param("start") LocalDateTime start,
                                    @Param("end") LocalDateTime end);

    @Query("""
            SELECT new ru.practicum.ViewStats(s.app, s.uri, COUNT(s.ip))
            FROM Hit s
            WHERE s.timestamp BETWEEN :start AND :end
            GROUP BY s.app, s.uri
            ORDER BY COUNT(s.ip) DESC
            """)
    List<ViewStats> findAllWithoutUris(@Param("start") LocalDateTime start,
                                       @Param("end") LocalDateTime end);

    @Query("""
            SELECT new ru.practicum.ViewStats(s.app, s.uri, COUNT(DISTINCT s.ip))
            FROM Hit s
            WHERE s.timestamp BETWEEN :start AND :end
              AND s.uri IN :uris
            GROUP BY s.app, s.uri
            ORDER BY COUNT(DISTINCT s.ip) DESC
            """)
    List<ViewStats> findAllUniqueWithUris(@Param("uris") List<String> uris,
                                          @Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end);

    @Query("""
            SELECT new ru.practicum.ViewStats(s.app, s.uri, COUNT(DISTINCT s.ip))
            FROM Hit s
            WHERE s.timestamp BETWEEN :start AND :end
            GROUP BY s.app, s.uri
            ORDER BY COUNT(DISTINCT s.ip) DESC
            """)
    List<ViewStats> findAllUniqueWithoutUris(@Param("start") LocalDateTime start,
                                             @Param("end") LocalDateTime end);
}
