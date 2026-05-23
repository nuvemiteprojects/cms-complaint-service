package com.nuvemite.cms.complaints.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "inbox_processed_event")
@IdClass(InboxProcessedEvent.InboxProcessedEventId.class)
public class InboxProcessedEvent {

    @Id
    @Column(name = "event_id")
    private String eventId;

    @Id
    @Column(name = "consumer_group")
    private String consumerGroup;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;

    protected InboxProcessedEvent() {}

    public static InboxProcessedEvent create(String eventId, String consumerGroup) {
        InboxProcessedEvent record = new InboxProcessedEvent();
        record.eventId = eventId;
        record.consumerGroup = consumerGroup;
        record.processedAt = Instant.now();
        return record;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public record InboxProcessedEventId(String eventId, String consumerGroup) implements Serializable {
        public InboxProcessedEventId() {
            this(null, null);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InboxProcessedEvent that)) {
            return false;
        }
        return Objects.equals(eventId, that.eventId) && Objects.equals(consumerGroup, that.consumerGroup);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, consumerGroup);
    }
}
