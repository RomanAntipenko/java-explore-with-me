package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.enums.EventStatus;
import ru.practicum.ewm.model.Event;

import java.util.List;
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    Event findFirstByCategoryId(Long catId);

    List<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);

    Event findByInitiatorIdAndId(Long initiatorId, Long id);

    Page<Event> findAll(Specification<Event> specification, Pageable pageable);

    boolean existsByInitiatorIdAndId(Long userId, Long eventId);

    Event findFirstByIdAndState(Long eventId, EventStatus eventStatus);


    List<Event> findAllByIdIn(Set<Long> events);
}
