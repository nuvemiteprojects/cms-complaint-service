package com.nuvemite.cms.complaints.web.dto;

import jakarta.validation.constraints.NotBlank;

public record RejectComplaintRequest(@NotBlank String notes) {}
