package com.nuvemite.cms.complaints.web.dto;

import com.nuvemite.cms.complaints.domain.ComplaintPriority;
import com.nuvemite.cms.complaints.domain.ComplaintStatus;
import com.nuvemite.cms.complaints.domain.ResolutionOutcome;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ComplaintResponse(
        UUID id,
        String referenceNumber,
        ComplaintStatus status,
        String title,
        String description,
        String category,
        ComplaintPriority priority,
        String locationTitle,
        String countryCode,
        String addressLine,
        String postalCode,
        LocationDto location,
        UUID primaryCompanyId,
        UUID primaryPremiseId,
        String primaryCompanyName,
        String primaryPremiseName,
        List<RelatedSubjectResponse> relatedSubjects,
        String reporterUserSub,
        String reporterName,
        String reporterEmail,
        String reporterPhone,
        String assignedRegulatorSub,
        ResolutionOutcome resolutionOutcome,
        String resolutionNotes,
        boolean requestSpecialInspection,
        Instant submittedAt,
        Instant resolvedAt,
        Instant createdAt,
        Instant updatedAt,
        long version) {}
