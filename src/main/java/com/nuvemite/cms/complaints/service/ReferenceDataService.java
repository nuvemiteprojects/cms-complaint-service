package com.nuvemite.cms.complaints.service;

import com.nuvemite.cms.complaints.domain.CompanyReference;
import com.nuvemite.cms.complaints.domain.PremiseReference;
import com.nuvemite.cms.complaints.repository.CompanyReferenceRepository;
import com.nuvemite.cms.complaints.repository.PremiseReferenceRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReferenceDataService {

    private final CompanyReferenceRepository companyReferenceRepository;
    private final PremiseReferenceRepository premiseReferenceRepository;

    public ReferenceDataService(
            CompanyReferenceRepository companyReferenceRepository,
            PremiseReferenceRepository premiseReferenceRepository) {
        this.companyReferenceRepository = companyReferenceRepository;
        this.premiseReferenceRepository = premiseReferenceRepository;
    }

    @Transactional
    public void registerCompany(UUID companyId, String legalName) {
        companyReferenceRepository
                .findById(companyId)
                .ifPresentOrElse(
                        existing -> {
                            if (legalName != null) {
                                existing.setLegalName(legalName);
                                companyReferenceRepository.save(existing);
                            }
                        },
                        () -> companyReferenceRepository.save(CompanyReference.of(companyId, legalName)));
    }

    @Transactional
    public void registerPremise(UUID premiseId, UUID companyId, String name) {
        premiseReferenceRepository
                .findById(premiseId)
                .ifPresentOrElse(
                        existing -> premiseReferenceRepository.save(PremiseReference.of(premiseId, companyId, name)),
                        () -> premiseReferenceRepository.save(PremiseReference.of(premiseId, companyId, name)));
    }

    @Transactional(readOnly = true)
    public String companyName(UUID companyId) {
        return companyReferenceRepository.findById(companyId).map(CompanyReference::getLegalName).orElse(null);
    }

    @Transactional(readOnly = true)
    public String premiseName(UUID premiseId) {
        return premiseReferenceRepository.findById(premiseId).map(PremiseReference::getName).orElse(null);
    }
}
