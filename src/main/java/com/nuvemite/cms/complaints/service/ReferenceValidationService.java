package com.nuvemite.cms.complaints.service;

import com.nuvemite.cms.complaints.domain.PremiseReference;
import com.nuvemite.cms.complaints.exception.UnprocessableEntityException;
import com.nuvemite.cms.complaints.repository.CompanyReferenceRepository;
import com.nuvemite.cms.complaints.repository.PremiseReferenceRepository;
import com.nuvemite.cms.complaints.web.dto.SubjectRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ReferenceValidationService {

    private final CompanyReferenceRepository companyReferenceRepository;
    private final PremiseReferenceRepository premiseReferenceRepository;
    private final ReferenceDataService referenceDataService;

    public ReferenceValidationService(
            CompanyReferenceRepository companyReferenceRepository,
            PremiseReferenceRepository premiseReferenceRepository,
            ReferenceDataService referenceDataService) {
        this.companyReferenceRepository = companyReferenceRepository;
        this.premiseReferenceRepository = premiseReferenceRepository;
        this.referenceDataService = referenceDataService;
    }

    public ResolvedSubject resolvePrimary(SubjectRequest subject) {
        if (subject == null || subject.companyId() == null) {
            return ResolvedSubject.empty();
        }
        return resolve(subject);
    }

    public List<ResolvedSubject> resolveRelated(List<SubjectRequest> subjects) {
        if (subjects == null || subjects.isEmpty()) {
            return List.of();
        }
        return subjects.stream().map(this::resolve).toList();
    }

    private ResolvedSubject resolve(SubjectRequest subject) {
        requireCompany(subject.companyId());
        String companyName = referenceDataService.companyName(subject.companyId());
        UUID premiseId = subject.premiseId();
        String premiseName = null;
        if (premiseId != null) {
            PremiseReference premise = premiseReferenceRepository
                    .findByPremiseIdAndCompanyId(premiseId, subject.companyId())
                    .orElseThrow(() -> new UnprocessableEntityException(
                            "Unknown premise " + premiseId + " for company " + subject.companyId()));
            premiseName = premise.getName();
        }
        return new ResolvedSubject(subject.companyId(), premiseId, companyName, premiseName);
    }

    private void requireCompany(UUID companyId) {
        if (!companyReferenceRepository.existsById(companyId)) {
            throw new UnprocessableEntityException("Unknown company: " + companyId);
        }
    }

    public record ResolvedSubject(UUID companyId, UUID premiseId, String companyName, String premiseName) {
        public static ResolvedSubject empty() {
            return new ResolvedSubject(null, null, null, null);
        }

        public boolean isEmpty() {
            return companyId == null;
        }
    }
}
