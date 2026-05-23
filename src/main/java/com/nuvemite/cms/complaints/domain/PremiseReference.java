package com.nuvemite.cms.complaints.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "premise_reference")
public class PremiseReference {

    @Id
    @Column(name = "premise_id")
    private UUID premiseId;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    private String name;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected PremiseReference() {}

    public static PremiseReference of(UUID premiseId, UUID companyId, String name) {
        PremiseReference ref = new PremiseReference();
        ref.premiseId = premiseId;
        ref.companyId = companyId;
        ref.name = name;
        ref.createdAt = Instant.now();
        return ref;
    }

    public UUID getPremiseId() {
        return premiseId;
    }

    public UUID getCompanyId() {
        return companyId;
    }

    public String getName() {
        return name;
    }
}
