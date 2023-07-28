package ru.practicum.ewm.model;

import lombok.*;
import ru.practicum.ewm.enums.EventStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "events")
@EqualsAndHashCode
public class Event {
    @Column
    private String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @Column(name = "confirmed_requests")
    private Long confirmedRequests;
    @JoinColumn(name = "created_on")
    private LocalDateTime createdOn;
    @Column
    private String description;
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;
    @OneToOne
    @JoinColumn(name = "location_id")
    private Location location;
    @Column
    private Boolean paid;
    @Column(name = "participant_limit")
    private Long participantLimit;
    @Column(name = "published_on")
    private LocalDateTime publishedOn;
    @Column(name = "request_moderation")
    private Boolean requestModeration;
    @Enumerated(EnumType.STRING)
    @Column
    private EventStatus state;
    @Column
    private String title;
    @Column
    private Long views;
}
