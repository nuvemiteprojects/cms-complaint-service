package com.nuvemite.cms.complaints.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddAttachmentRequest(
        @NotBlank @Size(max = 255) String documentName,
        @Size(max = 64) String documentType,
        @NotBlank @Size(max = 512) String storageRef) {}
