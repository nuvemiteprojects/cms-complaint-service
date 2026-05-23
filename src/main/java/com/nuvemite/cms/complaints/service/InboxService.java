package com.nuvemite.cms.complaints.service;

import com.nuvemite.cms.complaints.domain.InboxProcessedEvent;
import com.nuvemite.cms.complaints.messaging.EventTypes;
import com.nuvemite.cms.complaints.repository.InboxProcessedEventRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InboxService {

    private final InboxProcessedEventRepository inboxProcessedEventRepository;

    public InboxService(InboxProcessedEventRepository inboxProcessedEventRepository) {
        this.inboxProcessedEventRepository = inboxProcessedEventRepository;
    }

    @Transactional(readOnly = true)
    public boolean isProcessed(String eventId) {
        return inboxProcessedEventRepository.existsByEventIdAndConsumerGroup(eventId, EventTypes.CONSUMER_GROUP);
    }

    @Transactional
    public boolean tryMarkProcessed(String eventId) {
        if (isProcessed(eventId)) {
            return false;
        }
        InboxProcessedEvent record = InboxProcessedEvent.create(eventId, EventTypes.CONSUMER_GROUP);
        try {
            inboxProcessedEventRepository.save(record);
            return true;
        } catch (DataIntegrityViolationException ex) {
            return false;
        }
    }
}
