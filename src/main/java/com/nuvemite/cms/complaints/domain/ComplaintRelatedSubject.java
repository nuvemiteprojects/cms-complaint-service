package com.nuvemite.cms.complaints.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "complaint_related_subject")
public class ComplaintRelatedSubject {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "complaint_id", nullable = false)
    private Complaint complaint;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "premise_id")
    private UUID premiseId;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "premise_name")
    private String premiseName;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected ComplaintRelatedSubject() {}

    public static ComplaintRelatedSubject create(
            Complaint complaint, UUID companyId, UUID premiseId, String companyName, String premiseName) {
        ComplaintRelatedSubject subject = new ComplaintRelatedSubject();
        subject.id = UUID.randomUUID();
        subject.complaint = complaint;
        subject.companyId = companyId;
        subject.premiseId = premiseId;
        subject.companyName = companyName;
        subject.premiseName = premiseName;
        subject.createdAt = Instant.now();
        return subject;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCompanyId() {
        return companyId;
    }

    public UUID getPremiseId() {
        return premiseId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getPremiseName() {
        return premiseName;
    }

    void attachTo(Complaint complaint) {
        this.complaint = complaint;
    }
}
