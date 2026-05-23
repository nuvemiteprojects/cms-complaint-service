package com.nuvemite.cms.complaints.web.dto;

import java.time.Instant;
import java.util.UUID;

public record AttachmentResponse(
        UUID id,
        UUID complaintId,
        String documentName,
        String documentType,
        String storageRef,
        Instant uploadedAt,
        String uploadedBy) {}
