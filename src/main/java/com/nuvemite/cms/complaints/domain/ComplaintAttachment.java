package com.nuvemite.cms.complaints.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "complaint_attachment")
public class ComplaintAttachment {

    @Id
    private UUID id;

    @Column(name = "complaint_id", nullable = false)
    private UUID complaintId;

    @Column(name = "document_name", nullable = false)
    private String documentName;

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "storage_ref", nullable = false)
    private String storageRef;

    @Column(name = "uploaded_at", nullable = false)
    private Instant uploadedAt;

    @Column(name = "uploaded_by")
    private String uploadedBy;

    protected ComplaintAttachment() {}

    public static ComplaintAttachment create(
            UUID complaintId, String documentName, String documentType, String storageRef, String uploadedBy) {
        ComplaintAttachment attachment = new ComplaintAttachment();
        attachment.id = UUID.randomUUID();
        attachment.complaintId = complaintId;
        attachment.documentName = documentName;
        attachment.documentType = documentType;
        attachment.storageRef = storageRef;
        attachment.uploadedBy = uploadedBy;
        attachment.uploadedAt = Instant.now();
        return attachment;
    }

    public UUID getId() {
        return id;
    }

    public UUID getComplaintId() {
        return complaintId;
    }

    public String getDocumentName() {
        return documentName;
    }

    public String getDocumentType() {
        return documentType;
    }

    public String getStorageRef() {
        return storageRef;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }
}
