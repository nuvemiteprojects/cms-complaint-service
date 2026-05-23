package com.nuvemite.cms.complaints.web.dto;

import com.nuvemite.cms.complaints.domain.ComplaintPriority;
import jakarta.validation.constraints.NotBlank;

public record TriageComplaintRequest(
        @NotBlank String assignedRegulatorSub, ComplaintPriority priority, String notes) {}
