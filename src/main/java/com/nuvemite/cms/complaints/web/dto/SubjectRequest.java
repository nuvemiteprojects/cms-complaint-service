package com.nuvemite.cms.complaints.web.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record SubjectRequest(@NotNull UUID companyId, UUID premiseId) {}
