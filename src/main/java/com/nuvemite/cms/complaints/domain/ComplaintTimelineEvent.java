package com.nuvemite.cms.complaints.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "complaint_timeline_event")
public class ComplaintTimelineEvent {

    @Id
    private UUID id;

    @Column(name = "complaint_id", nullable = false)
    private UUID complaintId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "actor_ref")
    private String actorRef;

    private String notes;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    protected ComplaintTimelineEvent() {}

    public static ComplaintTimelineEvent create(UUID complaintId, String eventType, String actorRef, String notes) {
        ComplaintTimelineEvent event = new ComplaintTimelineEvent();
        event.id = UUID.randomUUID();
        event.complaintId = complaintId;
        event.eventType = eventType;
        event.actorRef = actorRef;
        event.notes = notes;
        event.occurredAt = Instant.now();
        return event;
    }

    public UUID getId() {
        return id;
    }

    public UUID getComplaintId() {
        return complaintId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getActorRef() {
        return actorRef;
    }

    public String getNotes() {
        return notes;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
