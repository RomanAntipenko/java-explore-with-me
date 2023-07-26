package ru.practicum.ewm.model;

import lombok.*;
import ru.practicum.ewm.enums.EventRequestStatus;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "requests")
@EqualsAndHashCode
public class Request {
    @Column
    private String created;
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
