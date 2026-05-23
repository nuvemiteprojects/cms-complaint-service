package com.nuvemite.cms.complaints.messaging.events;

import java.time.Instant;
import java.util.UUID;

public record ComplaintResolvedEvent(
        UUID eventId,
        String eventType,
        UUID complaintId,
        String referenceNumber,
        String resolutionOutcome,
        boolean requestSpecialInspection,
        Instant resolvedAt) {}
