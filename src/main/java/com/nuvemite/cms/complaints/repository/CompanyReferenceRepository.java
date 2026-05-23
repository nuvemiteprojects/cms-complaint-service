package com.nuvemite.cms.complaints.repository;

import com.nuvemite.cms.complaints.domain.CompanyReference;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyReferenceRepository extends JpaRepository<CompanyReference, UUID> {}
