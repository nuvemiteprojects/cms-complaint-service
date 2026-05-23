package com.nuvemite.cms.complaints.web.dto;

import java.util.UUID;

public record RelatedSubjectResponse(
        UUID id, UUID companyId, UUID premiseId, String companyName, String premiseName) {}
