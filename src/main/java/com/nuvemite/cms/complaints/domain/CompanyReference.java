package com.nuvemite.cms.complaints.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "company_reference")
public class CompanyReference {

    @Id
    @Column(name = "company_id")
    private UUID companyId;

    @Column(name = "legal_name")
    private String legalName;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected CompanyReference() {}

    public static CompanyReference of(UUID companyId, String legalName) {
        CompanyReference ref = new CompanyReference();
        ref.companyId = companyId;
        ref.legalName = legalName;
        ref.createdAt = Instant.now();
        return ref;
    }

    public UUID getCompanyId() {
        return companyId;
    }

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }
}
