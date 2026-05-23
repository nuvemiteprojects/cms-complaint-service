package com.nuvemite.cms.complaints.domain;

import com.nuvemite.cms.complaints.util.GeoUtils;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "complaint")
public class Complaint {

    @Id
    private UUID id;

    @Column(name = "reference_number", nullable = false, unique = true)
    private String referenceNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplaintStatus status = ComplaintStatus.DRAFT;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    private String category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplaintPriority priority = ComplaintPriority.MEDIUM;

    @Column(name = "location_title")
    private String locationTitle;

    @Column(name = "country_code", length = 2)
    private String countryCode;

    @Column(name = "address_line")
    private String addressLine;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(columnDefinition = "geography(Point,4326)")
    private Point location;

    @Column(name = "primary_company_id")
    private UUID primaryCompanyId;

    @Column(name = "primary_premise_id")
    private UUID primaryPremiseId;

    @Column(name = "primary_company_name")
    private String primaryCompanyName;

    @Column(name = "primary_premise_name")
    private String primaryPremiseName;

    @Column(name = "reporter_user_sub", nullable = false)
    private String reporterUserSub;

    @Column(name = "reporter_name")
    private String reporterName;

    @Column(name = "reporter_email")
    private String reporterEmail;

    @Column(name = "reporter_phone")
    private String reporterPhone;

    @Column(name = "assigned_regulator_sub")
    private String assignedRegulatorSub;

    @Enumerated(EnumType.STRING)
    @Column(name = "resolution_outcome")
    private ResolutionOutcome resolutionOutcome;

    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;

    @Column(name = "request_special_inspection", nullable = false)
    private boolean requestSpecialInspection;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    @Version
    private long version;

    @OneToMany(mappedBy = "complaint", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComplaintRelatedSubject> relatedSubjects = new ArrayList<>();

    protected Complaint() {}

    public static Complaint createDraft(
            String referenceNumber,
            String title,
            String description,
            String category,
            ComplaintPriority priority,
            String locationTitle,
            String countryCode,
            String addressLine,
            String postalCode,
            Point location,
            UUID primaryCompanyId,
            UUID primaryPremiseId,
            String primaryCompanyName,
            String primaryPremiseName,
            String reporterUserSub,
            String reporterName,
            String reporterEmail,
            String reporterPhone,
            String createdBy) {
        Complaint complaint = new Complaint();
        complaint.id = UUID.randomUUID();
        complaint.referenceNumber = referenceNumber;
        complaint.title = title;
        complaint.description = description;
        complaint.category = category;
        complaint.priority = priority != null ? priority : ComplaintPriority.MEDIUM;
        complaint.locationTitle = locationTitle;
        complaint.countryCode = countryCode;
        complaint.addressLine = addressLine;
        complaint.postalCode = postalCode;
        complaint.location = location;
        complaint.primaryCompanyId = primaryCompanyId;
        complaint.primaryPremiseId = primaryPremiseId;
        complaint.primaryCompanyName = primaryCompanyName;
        complaint.primaryPremiseName = primaryPremiseName;
        complaint.reporterUserSub = reporterUserSub;
        complaint.reporterName = reporterName;
        complaint.reporterEmail = reporterEmail;
        complaint.reporterPhone = reporterPhone;
        complaint.createdBy = createdBy;
        return complaint;
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public void replaceRelatedSubjects(List<ComplaintRelatedSubject> subjects) {
        relatedSubjects.clear();
        if (subjects != null) {
            subjects.forEach(s -> {
                s.attachTo(this);
                relatedSubjects.add(s);
            });
        }
    }

    public void updateDraft(
            String title,
            String description,
            String category,
            ComplaintPriority priority,
            String locationTitle,
            String countryCode,
            String addressLine,
            String postalCode,
            Point location) {
        requireStatus(ComplaintStatus.DRAFT);
        this.title = title;
        this.description = description;
        this.category = category;
        if (priority != null) {
            this.priority = priority;
        }
        this.locationTitle = locationTitle;
        this.countryCode = countryCode;
        this.addressLine = addressLine;
        this.postalCode = postalCode;
        this.location = location;
    }

    public void submit() {
        requireStatus(ComplaintStatus.DRAFT);
        status = ComplaintStatus.SUBMITTED;
        submittedAt = Instant.now();
    }

    public void triage(String assignedRegulatorSub, ComplaintPriority priority) {
        requireStatus(ComplaintStatus.SUBMITTED);
        status = ComplaintStatus.TRIAGED;
        this.assignedRegulatorSub = assignedRegulatorSub;
        if (priority != null) {
            this.priority = priority;
        }
    }

    public void startInvestigation() {
        requireStatus(ComplaintStatus.TRIAGED, ComplaintStatus.REQUIRES_MORE_INFO);
        status = ComplaintStatus.UNDER_INVESTIGATION;
    }

    public void requestMoreInfo() {
        requireStatus(
                ComplaintStatus.TRIAGED,
                ComplaintStatus.UNDER_INVESTIGATION,
                ComplaintStatus.SUBMITTED);
        status = ComplaintStatus.REQUIRES_MORE_INFO;
    }

    public void respondToInfoRequest() {
        requireStatus(ComplaintStatus.REQUIRES_MORE_INFO);
        status = ComplaintStatus.UNDER_INVESTIGATION;
    }

    public void reject(String notes) {
        requireStatus(ComplaintStatus.SUBMITTED, ComplaintStatus.TRIAGED);
        status = ComplaintStatus.REJECTED;
        resolutionNotes = notes;
        resolvedAt = Instant.now();
    }

    public void resolve(ResolutionOutcome outcome, String notes, boolean requestSpecialInspection) {
        requireStatus(
                ComplaintStatus.TRIAGED,
                ComplaintStatus.UNDER_INVESTIGATION,
                ComplaintStatus.REQUIRES_MORE_INFO);
        status = ComplaintStatus.RESOLVED;
        resolutionOutcome = outcome;
        resolutionNotes = notes;
        this.requestSpecialInspection = requestSpecialInspection;
        resolvedAt = Instant.now();
    }

    public void closeNoAction(String notes) {
        requireStatus(
                ComplaintStatus.TRIAGED,
                ComplaintStatus.UNDER_INVESTIGATION,
                ComplaintStatus.REQUIRES_MORE_INFO);
        status = ComplaintStatus.CLOSED_NO_ACTION;
        resolutionOutcome = ResolutionOutcome.NO_ACTION;
        resolutionNotes = notes;
        requestSpecialInspection = false;
        resolvedAt = Instant.now();
    }

    private void requireStatus(ComplaintStatus... allowed) {
        for (ComplaintStatus s : allowed) {
            if (status == s) {
                return;
            }
        }
        throw new IllegalStateException("Invalid status transition from " + status);
    }

    public UUID getId() {
        return id;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public ComplaintStatus getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public ComplaintPriority getPriority() {
        return priority;
    }

    public String getLocationTitle() {
        return locationTitle;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public Point getLocation() {
        return location;
    }

    public UUID getPrimaryCompanyId() {
        return primaryCompanyId;
    }

    public UUID getPrimaryPremiseId() {
        return primaryPremiseId;
    }

    public String getPrimaryCompanyName() {
        return primaryCompanyName;
    }

    public String getPrimaryPremiseName() {
        return primaryPremiseName;
    }

    public String getReporterUserSub() {
        return reporterUserSub;
    }

    public String getReporterName() {
        return reporterName;
    }

    public String getReporterEmail() {
        return reporterEmail;
    }

    public String getReporterPhone() {
        return reporterPhone;
    }

    public String getAssignedRegulatorSub() {
        return assignedRegulatorSub;
    }

    public ResolutionOutcome getResolutionOutcome() {
        return resolutionOutcome;
    }

    public String getResolutionNotes() {
        return resolutionNotes;
    }

    public boolean isRequestSpecialInspection() {
        return requestSpecialInspection;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public Instant getResolvedAt() {
        return resolvedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public long getVersion() {
        return version;
    }

    public List<ComplaintRelatedSubject> getRelatedSubjects() {
        return List.copyOf(relatedSubjects);
    }

    public Double getLatitude() {
        return GeoUtils.latitude(location);
    }

    public Double getLongitude() {
        return GeoUtils.longitude(location);
    }
}
