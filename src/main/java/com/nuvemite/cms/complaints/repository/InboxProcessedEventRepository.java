package com.nuvemite.cms.complaints.repository;

import com.nuvemite.cms.complaints.domain.InboxProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InboxProcessedEventRepository
        extends JpaRepository<InboxProcessedEvent, InboxProcessedEvent.InboxProcessedEventId> {

    boolean existsByEventIdAndConsumerGroup(String eventId, String consumerGroup);
}
