package com.nuvemite.cms.complaints.messaging.events;

import java.time.Instant;
import java.util.UUID;

public record ComplaintInspectionRequestedEvent(
        UUID eventId,
        String eventType,
        UUID complaintId,
        String referenceNumber,
        UUID companyId,
        UUID premiseId,
        String premiseName,
        String inspectionType,
        String reason,
        String requestedBy,
        Instant requestedAt) {}
