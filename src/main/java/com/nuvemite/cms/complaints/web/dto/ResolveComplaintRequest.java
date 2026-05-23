package com.nuvemite.cms.complaints.web.dto;

import com.nuvemite.cms.complaints.domain.ResolutionOutcome;
import jakarta.validation.constraints.NotNull;

public record ResolveComplaintRequest(
        @NotNull ResolutionOutcome outcome, String notes, boolean requestSpecialInspection) {}
