package ru.practicum.ewm.model;

import lombok.*;
import ru.practicum.ewm.enums.EventRequestStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "requests")
@EqualsAndHashCode
public class Request {
    @Column
    private LocalDateTime created;
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User requester;
    @Enumerated(EnumType.STRING)
    @Column
    private EventRequestStatus status;
}
