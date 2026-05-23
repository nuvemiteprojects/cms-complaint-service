package com.nuvemite.cms.complaints.repository;

import com.nuvemite.cms.complaints.domain.PremiseReference;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PremiseReferenceRepository extends JpaRepository<PremiseReference, UUID> {

    Optional<PremiseReference> findByPremiseIdAndCompanyId(UUID premiseId, UUID companyId);
}
