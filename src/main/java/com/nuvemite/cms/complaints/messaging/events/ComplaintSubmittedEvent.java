package com.nuvemite.cms.complaints.messaging.events;

import java.time.Instant;
import java.util.UUID;

public record ComplaintSubmittedEvent(
        UUID eventId,
        String eventType,
        UUID complaintId,
        String referenceNumber,
        String reporterUserSub,
        Instant submittedAt) {}
