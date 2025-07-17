package ru.practicum.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ViewStatsProjection;
import ru.practicum.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HitRepository extends JpaRepository<Hit, Long> {

    @Query("""
            SELECT s.app AS app, s.uri AS uri, COUNT(s.ip) AS hits
            FROM Hit s
            WHERE s.timestamp BETWEEN :start AND :end
              AND s.uri IN :uris
            GROUP BY s.app, s.uri
            ORDER BY hits DESC
            """)
    List<ViewStatsProjection> findAllWithUris(@Param("uris") List<String> uris,
                                              @Param("start") LocalDateTime start,
                                              @Param("end") LocalDateTime end);

    @Query("""
            SELECT s.app AS app, s.uri AS uri, COUNT(s.ip) AS hits
            FROM Hit s
            WHERE s.timestamp BETWEEN :start AND :end
            GROUP BY s.app, s.uri
            ORDER BY hits DESC
            """)
    List<ViewStatsProjection> findAllWithoutUris(@Param("start") LocalDateTime start,
                                                 @Param("end") LocalDateTime end);

    @Query("""
            SELECT s.app AS app, s.uri AS uri, COUNT(DISTINCT s.ip) AS hits
            FROM Hit s
            WHERE s.timestamp BETWEEN :start AND :end
              AND s.uri IN :uris
            GROUP BY s.app, s.uri
            ORDER BY hits DESC
            """)
    List<ViewStatsProjection> findAllUniqueWithUris(@Param("uris") List<String> uris,
                                                    @Param("start") LocalDateTime start,
                                                    @Param("end") LocalDateTime end);

    @Query("""
            SELECT s.app AS app, s.uri AS uri, COUNT(DISTINCT s.ip) AS hits
            FROM Hit s
            WHERE s.timestamp BETWEEN :start AND :end
            GROUP BY s.app, s.uri
            ORDER BY hits DESC
            """)
    List<ViewStatsProjection> findAllUniqueWithoutUris(@Param("start") LocalDateTime start,
                                                       @Param("end") LocalDateTime end);
}

