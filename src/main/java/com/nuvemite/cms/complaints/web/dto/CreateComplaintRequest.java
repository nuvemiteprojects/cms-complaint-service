package com.nuvemite.cms.complaints.web.dto;

import com.nuvemite.cms.complaints.domain.ComplaintPriority;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreateComplaintRequest(
        @NotBlank @Size(max = 255) String title,
        @NotBlank String description,
        @Size(max = 128) String category,
        ComplaintPriority priority,
        @Size(max = 255) String locationTitle,
        @Size(max = 2) String countryCode,
        @Size(max = 512) String addressLine,
        @Size(max = 32) String postalCode,
        @Valid LocationDto location,
        @Valid SubjectRequest primarySubject,
        List<@Valid SubjectRequest> relatedSubjects,
        @Size(max = 255) String reporterName,
        @Size(max = 255) String reporterEmail,
        @Size(max = 64) String reporterPhone) {}
