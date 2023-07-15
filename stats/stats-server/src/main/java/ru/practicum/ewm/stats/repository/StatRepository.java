package ru.practicum.ewm.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.stats.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatRepository extends JpaRepository<Hit, Long> {
    @Query("SELECT h " +
            "FROM Hit AS h " +
            "WHERE h.created BETWEEN ?1 AND ?2 AND h.uri IN ?3 ")
    List<Hit> findStatsWithUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT h " +
            "FROM Hit AS h " +
            "WHERE h.created BETWEEN ?1 AND ?2 ")
    List<Hit> findStatsWithoutUris(LocalDateTime start, LocalDateTime end);
}
