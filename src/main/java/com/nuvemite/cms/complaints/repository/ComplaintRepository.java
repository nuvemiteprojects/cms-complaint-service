package com.nuvemite.cms.complaints.repository;

import com.nuvemite.cms.complaints.domain.Complaint;
import com.nuvemite.cms.complaints.domain.ComplaintPriority;
import com.nuvemite.cms.complaints.domain.ComplaintStatus;
import java.util.Collection;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ComplaintRepository extends JpaRepository<Complaint, UUID> {

    long count();

    @Query("""
            SELECT DISTINCT c FROM Complaint c
            LEFT JOIN c.relatedSubjects rs
            WHERE (:status IS NULL OR c.status = :status)
              AND (:priority IS NULL OR c.priority = :priority)
              AND (:category IS NULL OR c.category = :category)
              AND (:reporterSub IS NULL OR c.reporterUserSub = :reporterSub)
              AND (:assignedRegulator IS NULL OR c.assignedRegulatorSub = :assignedRegulator)
              AND (:primaryCompanyId IS NULL OR c.primaryCompanyId = :primaryCompanyId)
              AND (:primaryPremiseId IS NULL OR c.primaryPremiseId = :primaryPremiseId)
              AND (
                :companyScope IS NULL
                OR c.primaryCompanyId IN :companyScope
                OR rs.companyId IN :companyScope
              )
            ORDER BY c.createdAt DESC
            """)
    Page<Complaint> search(
            ComplaintStatus status,
            ComplaintPriority priority,
            String category,
            String reporterSub,
            String assignedRegulator,
            UUID primaryCompanyId,
            UUID primaryPremiseId,
            Collection<UUID> companyScope,
            Pageable pageable);
}
