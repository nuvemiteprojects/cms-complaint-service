package com.nuvemite.cms.complaints.service;

import com.nuvemite.cms.complaints.config.KafkaTopicsProperties;
import com.nuvemite.cms.complaints.domain.Complaint;
import com.nuvemite.cms.complaints.domain.ComplaintAttachment;
import com.nuvemite.cms.complaints.domain.ComplaintPriority;
import com.nuvemite.cms.complaints.domain.ComplaintRelatedSubject;
import com.nuvemite.cms.complaints.domain.ComplaintStatus;
import com.nuvemite.cms.complaints.domain.ComplaintTimelineEvent;
import com.nuvemite.cms.complaints.domain.ResolutionOutcome;
import com.nuvemite.cms.complaints.exception.ResourceNotFoundException;
import com.nuvemite.cms.complaints.exception.UnprocessableEntityException;
import com.nuvemite.cms.complaints.messaging.EventTypes;
import com.nuvemite.cms.complaints.messaging.events.ComplaintInspectionRequestedEvent;
import com.nuvemite.cms.complaints.messaging.events.ComplaintResolvedEvent;
import com.nuvemite.cms.complaints.messaging.events.ComplaintSubmittedEvent;
import com.nuvemite.cms.complaints.repository.ComplaintAttachmentRepository;
import com.nuvemite.cms.complaints.repository.ComplaintRepository;
import com.nuvemite.cms.complaints.repository.ComplaintTimelineEventRepository;
import com.nuvemite.cms.complaints.security.CmsUserPrincipal;
import com.nuvemite.cms.complaints.security.SecurityUtils;
import com.nuvemite.cms.complaints.util.GeoUtils;
import com.nuvemite.cms.complaints.web.dto.AddAttachmentRequest;
import com.nuvemite.cms.complaints.web.dto.CreateComplaintRequest;
import com.nuvemite.cms.complaints.web.dto.RequestInfoRequest;
import com.nuvemite.cms.complaints.web.dto.ResolveComplaintRequest;
import com.nuvemite.cms.complaints.web.dto.TriageComplaintRequest;
import com.nuvemite.cms.complaints.web.dto.UpdateComplaintRequest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final ComplaintTimelineEventRepository timelineRepository;
    private final ComplaintAttachmentRepository attachmentRepository;
    private final ComplaintReferenceNumberGenerator referenceNumberGenerator;
    private final ReferenceValidationService referenceValidationService;
    private final OutboxService outboxService;
    private final KafkaTopicsProperties topics;

    public ComplaintService(
            ComplaintRepository complaintRepository,
            ComplaintTimelineEventRepository timelineRepository,
            ComplaintAttachmentRepository attachmentRepository,
            ComplaintReferenceNumberGenerator referenceNumberGenerator,
            ReferenceValidationService referenceValidationService,
            OutboxService outboxService,
            KafkaTopicsProperties topics) {
        this.complaintRepository = complaintRepository;
        this.timelineRepository = timelineRepository;
        this.attachmentRepository = attachmentRepository;
        this.referenceNumberGenerator = referenceNumberGenerator;
        this.referenceValidationService = referenceValidationService;
        this.outboxService = outboxService;
        this.topics = topics;
    }

    @Transactional(readOnly = true)
    public Page<Complaint> list(
            ComplaintStatus status,
            ComplaintPriority priority,
            String category,
            UUID primaryCompanyId,
            UUID primaryPremiseId,
            String assignedRegulator,
            Pageable pageable) {
        CmsUserPrincipal user = SecurityUtils.currentUser();
        String reporterSub = null;
        Collection<UUID> companyScope = null;

        if (user.isRegulator()) {
            // full access
        } else if (!user.companyIds().isEmpty()) {
            companyScope = user.companyIds();
        } else {
            reporterSub = user.subject();
        }

        return complaintRepository.search(
                status,
                priority,
                category,
                reporterSub,
                assignedRegulator,
                primaryCompanyId,
                primaryPremiseId,
                companyScope,
                pageable);
    }

    @Transactional(readOnly = true)
    public Complaint get(UUID id) {
        return findComplaint(id);
    }

    @Transactional(readOnly = true)
    public List<ComplaintTimelineEvent> timeline(UUID complaintId) {
        findComplaint(complaintId);
        return timelineRepository.findByComplaintIdOrderByOccurredAtAsc(complaintId);
    }

    @Transactional(readOnly = true)
    public List<ComplaintAttachment> attachments(UUID complaintId) {
        findComplaint(complaintId);
        return attachmentRepository.findByComplaintIdOrderByUploadedAtAsc(complaintId);
    }

    @Transactional
    public Complaint create(CreateComplaintRequest request, String actorSub) {
        ReferenceValidationService.ResolvedSubject primary =
                referenceValidationService.resolvePrimary(request.primarySubject());
        List<ReferenceValidationService.ResolvedSubject> related =
                referenceValidationService.resolveRelated(request.relatedSubjects());

        Point location = toPoint(request.location());
        String referenceNumber = referenceNumberGenerator.next(complaintRepository.count());

        Complaint complaint = Complaint.createDraft(
                referenceNumber,
                request.title(),
                request.description(),
                request.category(),
                request.priority(),
                request.locationTitle(),
                request.countryCode(),
                request.addressLine(),
                request.postalCode(),
                location,
                primary.companyId(),
                primary.premiseId(),
                primary.companyName(),
                primary.premiseName(),
                actorSub,
                request.reporterName(),
                request.reporterEmail(),
                request.reporterPhone(),
                actorSub);

        complaint.replaceRelatedSubjects(buildRelatedSubjects(complaint, related));
        complaintRepository.save(complaint);
        recordTimeline(complaint.getId(), "COMPLAINT_CREATED", actorSub, null);
        return complaint;
    }

    @Transactional
    public Complaint updateDraft(UUID id, UpdateComplaintRequest request) {
        Complaint complaint = findComplaint(id);
        complaint.updateDraft(
                request.title(),
                request.description(),
                request.category(),
                request.priority(),
                request.locationTitle(),
                request.countryCode(),
                request.addressLine(),
                request.postalCode(),
                toPoint(request.location()));
        return complaintRepository.save(complaint);
    }

    @Transactional
    public Complaint submit(UUID id, String actor) {
        Complaint complaint = findComplaint(id);
        complaint.submit();
        complaintRepository.save(complaint);
        recordTimeline(complaint.getId(), "COMPLAINT_SUBMITTED", actor, null);

        UUID eventId = UUID.randomUUID();
        outboxService.enqueue(
                "complaint",
                complaint.getId(),
                topics.complaintSubmitted(),
                new ComplaintSubmittedEvent(
                        eventId,
                        topics.complaintSubmitted(),
                        complaint.getId(),
                        complaint.getReferenceNumber(),
                        complaint.getReporterUserSub(),
                        complaint.getSubmittedAt()));
        return complaint;
    }

    @Transactional
    public Complaint triage(UUID id, TriageComplaintRequest request, String actor) {
        Complaint complaint = findComplaint(id);
        complaint.triage(request.assignedRegulatorSub(), request.priority());
        complaintRepository.save(complaint);
        recordTimeline(complaint.getId(), "COMPLAINT_TRIAGED", actor, request.notes());
        return complaint;
    }

    @Transactional
    public Complaint startInvestigation(UUID id, String actor) {
        Complaint complaint = findComplaint(id);
        complaint.startInvestigation();
        complaintRepository.save(complaint);
        recordTimeline(complaint.getId(), "INVESTIGATION_STARTED", actor, null);
        return complaint;
    }

    @Transactional
    public Complaint requestInfo(UUID id, RequestInfoRequest request, String actor) {
        Complaint complaint = findComplaint(id);
        complaint.requestMoreInfo();
        complaintRepository.save(complaint);
        recordTimeline(complaint.getId(), "REQUIRES_MORE_INFO", actor, request.notes());
        return complaint;
    }

    @Transactional
    public Complaint respondInfo(UUID id, String actor) {
        Complaint complaint = findComplaint(id);
        complaint.respondToInfoRequest();
        complaintRepository.save(complaint);
        recordTimeline(complaint.getId(), "INFO_RESPONDED", actor, null);
        return complaint;
    }

    @Transactional
    public Complaint reject(UUID id, String notes, String actor) {
        Complaint complaint = findComplaint(id);
        complaint.reject(notes);
        complaintRepository.save(complaint);
        recordTimeline(complaint.getId(), "COMPLAINT_REJECTED", actor, notes);
        return complaint;
    }

    @Transactional
    public Complaint resolve(UUID id, ResolveComplaintRequest request, String actor) {
        Complaint complaint = findComplaint(id);
        if (request.requestSpecialInspection()) {
            ensurePremisesForInspection(complaint);
        }
        complaint.resolve(request.outcome(), request.notes(), request.requestSpecialInspection());
        complaintRepository.save(complaint);
        recordTimeline(complaint.getId(), "COMPLAINT_RESOLVED", actor, request.notes());

        publishResolved(complaint);
        if (request.requestSpecialInspection()) {
            publishInspectionRequests(complaint, actor, request.notes());
        }
        return complaint;
    }

    @Transactional
    public Complaint closeNoAction(UUID id, String notes, String actor) {
        Complaint complaint = findComplaint(id);
        complaint.closeNoAction(notes);
        complaintRepository.save(complaint);
        recordTimeline(complaint.getId(), "CLOSED_NO_ACTION", actor, notes);
        publishResolved(complaint);
        return complaint;
    }

    @Transactional
    public ComplaintAttachment addAttachment(UUID complaintId, AddAttachmentRequest request, String actor) {
        findComplaint(complaintId);
        ComplaintAttachment attachment = ComplaintAttachment.create(
                complaintId,
                request.documentName(),
                request.documentType(),
                request.storageRef(),
                actor);
        return attachmentRepository.save(attachment);
    }

    private void publishResolved(Complaint complaint) {
        UUID eventId = UUID.randomUUID();
        outboxService.enqueue(
                "complaint",
                complaint.getId(),
                topics.complaintResolved(),
                new ComplaintResolvedEvent(
                        eventId,
                        topics.complaintResolved(),
                        complaint.getId(),
                        complaint.getReferenceNumber(),
                        complaint.getResolutionOutcome() != null
                                ? complaint.getResolutionOutcome().name()
                                : null,
                        complaint.isRequestSpecialInspection(),
                        complaint.getResolvedAt()));
    }

    private void publishInspectionRequests(Complaint complaint, String actor, String reason) {
        for (InspectionTarget target : inspectionTargets(complaint)) {
            UUID eventId = UUID.randomUUID();
            outboxService.enqueue(
                    "complaint",
                    complaint.getId(),
                    topics.complaintInspectionRequested(),
                    new ComplaintInspectionRequestedEvent(
                            eventId,
                            EventTypes.COMPLAINT_INSPECTION_REQUESTED,
                            complaint.getId(),
                            complaint.getReferenceNumber(),
                            target.companyId(),
                            target.premiseId(),
                            target.premiseName(),
                            "SPECIAL",
                            reason,
                            actor,
                            Instant.now()));
        }
    }

    private void ensurePremisesForInspection(Complaint complaint) {
        if (inspectionTargets(complaint).isEmpty()) {
            throw new UnprocessableEntityException(
                    "Special inspection requires at least one subject premise on the complaint");
        }
    }

    private List<InspectionTarget> inspectionTargets(Complaint complaint) {
        List<InspectionTarget> targets = new ArrayList<>();
        if (complaint.getPrimaryPremiseId() != null) {
            targets.add(new InspectionTarget(
                    complaint.getPrimaryCompanyId(),
                    complaint.getPrimaryPremiseId(),
                    complaint.getPrimaryPremiseName()));
        }
        complaint.getRelatedSubjects().stream()
                .filter(s -> s.getPremiseId() != null)
                .forEach(s -> targets.add(new InspectionTarget(s.getCompanyId(), s.getPremiseId(), s.getPremiseName())));
        return targets;
    }

    private List<ComplaintRelatedSubject> buildRelatedSubjects(
            Complaint complaint, List<ReferenceValidationService.ResolvedSubject> related) {
        return related.stream()
                .map(r -> ComplaintRelatedSubject.create(
                        complaint, r.companyId(), r.premiseId(), r.companyName(), r.premiseName()))
                .toList();
    }

    private Point toPoint(com.nuvemite.cms.complaints.web.dto.LocationDto location) {
        if (location == null) {
            return null;
        }
        return GeoUtils.toPoint(location.latitude(), location.longitude());
    }

    private Complaint findComplaint(UUID id) {
        return complaintRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found: " + id));
    }

    private void recordTimeline(UUID complaintId, String eventType, String actor, String notes) {
        timelineRepository.save(ComplaintTimelineEvent.create(complaintId, eventType, actor, notes));
    }

    private record InspectionTarget(UUID companyId, UUID premiseId, String premiseName) {}
}
