package com.nuvemite.cms.complaints.web.dto;

import jakarta.validation.constraints.NotBlank;

public record RequestInfoRequest(@NotBlank String notes) {}
